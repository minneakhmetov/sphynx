package com.razzzil.sphynx.worker.service;

import com.razzzil.sphynx.commons.model.database.DatabaseDisplayForm;
import com.razzzil.sphynx.commons.model.database.DatabaseModel;
import com.razzzil.sphynx.commons.model.database.configs.DatabaseConfigs;
import com.razzzil.sphynx.commons.model.iteration.configs.IterationConfigs;
import com.razzzil.sphynx.commons.model.savedquery.RequestQueryForm;
import com.razzzil.sphynx.commons.model.savedquery.SQLLabState;
import com.razzzil.sphynx.commons.model.savedquery.SavedQueryModel;
import com.razzzil.sphynx.commons.model.test.configs.TestConfigs;
import com.razzzil.sphynx.commons.model.testmode.TestMode;
import com.razzzil.sphynx.commons.model.worker.connectionstate.ConnectionState;
import com.razzzil.sphynx.commons.model.wrapper.TestUpdateStateWrapper;
import com.razzzil.sphynx.worker.model.TestConfigsWrapper;
import com.razzzil.sphynx.worker.service.thread.ProcessThread;
import com.razzzil.sphynx.worker.service.thread.ThreadEnv;
import com.razzzil.sphynx.worker.socket.SocketClient;
import com.razzzil.sphynx.worker.utils.WorkerUtils;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.razzzil.sphynx.commons.constant.StaticsConstants.OM;

@Service
@Scope("singleton")
public class TestsExecutionManager<T extends TestConfigs, V extends IterationConfigs, X extends TestMode.AbstractTestingModeConfig, Z extends DatabaseConfigs<? extends DataSource>> {

    @Value("${sphynx.maxTestsSameTime}")
    private Integer maxTestsSameTime;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private SocketClient socketClient;

    private Map<Integer, Pair<ProcessThread<T, V, X, Z>, TestConfigsWrapper<T, V, X, Z>>> running = new HashMap<>();
    private Map<Integer, Pair<ProcessThread<T, V, X, Z>, TestConfigsWrapper<T, V, X, Z>>> paused = new HashMap<>();
    private Queue<Pair<ProcessThread<T, V, X, Z>, TestConfigsWrapper<T, V, X, Z>>> ordered = new ConcurrentLinkedQueue<>();

    public void start(TestConfigsWrapper<T, V, X, Z> testConfigsWrapper) {
        if (running.size() < maxTestsSameTime) {
            ProcessThread<T, V, X, Z> processThread = createThread(testConfigsWrapper);
            processThread.start();
            running.put(testConfigsWrapper.getTestExecutionModel().getId(), Pair.of(processThread, testConfigsWrapper));
        } else {
            order(Pair.of(createThread(testConfigsWrapper), testConfigsWrapper));
        }
    }

    public void pauseTest(TestUpdateStateWrapper testUpdateStateWrapper) {
        Pair<ProcessThread<T, V, X, Z>, TestConfigsWrapper<T, V, X, Z>> pair = running.get(testUpdateStateWrapper.getId());
        pair.getLeft().pause();
        paused.put(testUpdateStateWrapper.getId(), pair);
        processQueue(testUpdateStateWrapper);
    }

    public void resumeTest(TestUpdateStateWrapper testUpdateStateWrapper) {
        Pair<ProcessThread<T, V, X, Z>, TestConfigsWrapper<T, V, X, Z>> pair = paused.get(testUpdateStateWrapper.getId());
        paused.remove(testUpdateStateWrapper.getId());
        if (running.size() > 0){
            order(pair);
        } else {
            running.put(testUpdateStateWrapper.getId(), pair);
            pair.getLeft().unpause();
        }
    }

    public void terminateTest(TestUpdateStateWrapper testUpdateStateWrapper) {
        Pair<ProcessThread<T, V, X, Z>, TestConfigsWrapper<T, V, X, Z>> runningPair = running.get(testUpdateStateWrapper.getId());
        if (Objects.nonNull(runningPair)) {
            runningPair.getLeft().kill();
            processQueue(testUpdateStateWrapper);
        } else {
            Pair<ProcessThread<T, V, X, Z>, TestConfigsWrapper<T, V, X, Z>> pausedPair = paused.get(testUpdateStateWrapper.getId());
            if (Objects.nonNull(pausedPair)){
                pausedPair.getLeft().kill();
                paused.remove(testUpdateStateWrapper.getId());
            } else {
                Pair<ProcessThread<T, V, X, Z>, TestConfigsWrapper<T, V, X, Z>> orderedPair = getByIdInOrdered(testUpdateStateWrapper.getId());
                if (Objects.nonNull(orderedPair)){
                    orderedPair.getLeft().kill();
                    ordered.remove(orderedPair);
                } else throw new IllegalStateException("Unable to terminate test: " + testUpdateStateWrapper.getId());
            }
        }
    }

    private void processQueue(TestUpdateStateWrapper testUpdateStateWrapper) {
        running.remove(testUpdateStateWrapper.getId());
        Pair<ProcessThread<T, V, X, Z>, TestConfigsWrapper<T, V, X, Z>> pair = ordered.poll();
        if (Objects.nonNull(pair)) {
            if (pair.getLeft().isPaused()) {
                pair.getLeft().unpause();
            } else {
                pair.getLeft().start();
            }
        }
    }

    private ProcessThread<T, V, X, Z> createThread(TestConfigsWrapper<T, V, X, Z> testConfigsWrapper) {
        ThreadEnv threadEnv = applicationContext.getBean(ThreadEnv.class);
        return new ProcessThread<>(threadEnv, testConfigsWrapper, this::processQueue);
    }

    private void order(Pair<ProcessThread<T, V, X, Z>, TestConfigsWrapper<T, V, X, Z>> pair){
        if (ordered.offer(pair)) {
            socketClient.sendMessage(pair.getRight().getUpdateWrapper(), "orderedTest");
        } else throw new IllegalStateException("Unable to add to queue: " + pair.getRight().getUpdateWrapper().getId());
    }

    private Pair<ProcessThread<T, V, X, Z>, TestConfigsWrapper<T, V, X, Z>> getByIdInOrdered(Integer executionId){
        return ordered
                .stream()
                .filter(pair -> pair.getRight().getTestExecutionModel().getId().equals(executionId))
                .findFirst()
                .orElse(null);
    }

    public DatabaseDisplayForm ping(DatabaseModel databaseModel) {
        return DatabaseDisplayForm.builder()
                .connectionState(getConnectionState(databaseModel))
                .id(databaseModel.getId())
                .userId(databaseModel.getUserId())
                .build();
    }

    @SneakyThrows
    private ConnectionState getConnectionState(DatabaseModel databaseModel) {
        Connection connection = null;
        try {
            Z databaseConfigs = databaseModel.deserialize();
            connection = databaseConfigs.datasource().getConnection();
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            boolean isReturnable = statement.execute(databaseModel.getType().getDatabaseDriver().getValidationQuery());
            connection.commit();
            ResultSet resultSet = isReturnable ? statement.getResultSet() : null;
            if (Objects.nonNull(resultSet)){
                connection.close();
                return ConnectionState.CONNECTED;
            } else {
                connection.close();
                return ConnectionState.ERROR;
            }
        } catch (SQLException e) {
            if (Objects.nonNull(connection)){
                connection.close();
            }
            return ConnectionState.DISCONNECTED;
        }
    }

    @SneakyThrows
    public SavedQueryModel query(RequestQueryForm requestQueryForm) {
        requestQueryForm.getSavedQueryModel().setTimeStart(LocalDateTime.now());
        Connection connection = null;
        try {
            Z databaseConfigs = requestQueryForm.getDatabaseModel().deserialize();
            connection = databaseConfigs.datasource().getConnection();
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            statement.execute(requestQueryForm.getSavedQueryModel().getSql());
            connection.commit();
            ResultSet resultSet = statement.getResultSet();
            requestQueryForm.getSavedQueryModel().setTimeEnd(LocalDateTime.now());
            requestQueryForm.getSavedQueryModel().setState(SQLLabState.SUCCESS);
            requestQueryForm.getSavedQueryModel().setResult(WorkerUtils.resultSetToJson(resultSet).toString());
            connection.close();
            return requestQueryForm.getSavedQueryModel();
        } catch (SQLException e) {
            if (Objects.nonNull(connection)){
                connection.close();
            }
            requestQueryForm.getSavedQueryModel().setTimeEnd(LocalDateTime.now());
            requestQueryForm.getSavedQueryModel().setState(SQLLabState.FAILED);
            requestQueryForm.getSavedQueryModel().setMessage(e.getClass().getName() + ": " + e.getMessage());
            return requestQueryForm.getSavedQueryModel();
        }
    }


}

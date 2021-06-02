package com.razzzil.sphynx.worker.service.processors.database;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.razzzil.sphynx.commons.model.database.DatabaseModel;
import com.razzzil.sphynx.commons.model.database.configs.MySQLConfigs;
import com.razzzil.sphynx.commons.model.execution.TestExecutionModel;
import com.razzzil.sphynx.commons.model.iteration.IterationModel;
import com.razzzil.sphynx.commons.model.iteration.configs.MySQLIteration;
import com.razzzil.sphynx.commons.model.key.WorkerCredential;
import com.razzzil.sphynx.commons.model.metrics.additional.AdditionalMetrics;
import com.razzzil.sphynx.commons.model.savedquery.SavedQueryModel;
import com.razzzil.sphynx.commons.model.test.configs.MySQLTestConfigs;
import com.razzzil.sphynx.commons.model.testmode.TestMode;
import com.razzzil.sphynx.commons.model.worker.connectionstate.ConnectionState;
import com.razzzil.sphynx.commons.model.wrapper.MetricsModelWrapper;
import com.razzzil.sphynx.worker.model.*;
import com.razzzil.sphynx.worker.socket.SocketClient;
import com.razzzil.sphynx.worker.utils.WorkerUtils;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Scope("prototype")
public class MySQLDatabaseProcessor implements DatabaseProcessorInterface<MySQLTestConfigs, MySQLIteration, MySQLConfigs> {

    @Value("${sphynx.maxSqlExecutionTimeout}")
    private Integer maxSqlExecutionTimeout;

    @Autowired
    private SocketClient socketClient;

    @Autowired
    private WorkerCredential workerCredential;

    private static final Logger LOGGER = LoggerFactory.getLogger(MySQLDatabaseProcessor.class);

    @SneakyThrows
    @Override
    public <X extends TestMode.AbstractTestingModeConfig> StepResult process(TestConfigsWrapper<MySQLTestConfigs, MySQLIteration, X, MySQLConfigs> testConfigsWrapper, Integer stepId, Function<ProcessModel, Integer> processCounter, List<Connection> connections) {
        LOGGER.info("Test: {}", testConfigsWrapper.getDatabaseConfigs());
        MysqlDataSource dataSource = testConfigsWrapper.getDatabaseConfigs().datasource();
        List<IterationWrapper<MySQLIteration>> iterations = testConfigsWrapper.getIterations();
        if (testConfigsWrapper.getTestConfigs().getConnect()) {
            for (int iterationNumber = 0; iterationNumber < iterations.size(); iterationNumber++) {
                IterationWrapper<MySQLIteration> iterationWrapper = iterations.get(iterationNumber);
                MySQLIteration iterationConfigs = iterationWrapper.getIterationConfigs();
                ExecutorService executorService = Executors.newFixedThreadPool(iterationConfigs.getClients());
                List<Callable<ClientResult>> clients = new ArrayList<>();
                for (int i = 0; i < iterationConfigs.getClients(); i++) {
                    long timeStart = System.currentTimeMillis();
                    Connection connection = dataSource.getConnection();
                    connection.setAutoCommit(false);
                    Callable<ClientResult> task = getTask(connection, iterationConfigs, timeStart, true, iterationWrapper.getIterationModel());
                    clients.add(task);
                }
                List<Future<ClientResult>> resultsCandidates = executorService.invokeAll(clients);
                StepResult preresult = sendMetrics(resultsCandidates, iterationConfigs, iterationNumber, stepId, testConfigsWrapper.getTestExecutionModel(),
                        testConfigsWrapper.getTestingModeConfigs().testMode(), iterationWrapper.getIterationModel(), processCounter, iterationWrapper.getSavedQueryModel(), iterations.size());
                if (!preresult.getSuccess()){
                    return preresult;
                }
            }
        } else {
            int maxClients = iterations
                    .stream()
                    .mapToInt(wrapper -> wrapper.getIterationConfigs().getClients())
                    .max()
                    .orElse(1);
            ExecutorService executorService = Executors.newFixedThreadPool(maxClients);
            List<Callable<ClientResult>> clients = new ArrayList<>();
            long timeStart = System.currentTimeMillis();
            if (Objects.isNull(connections) || connections.isEmpty()){
                connections = new ArrayList<>();
                for (int i = 0; i < maxClients; i++) {
                    Connection connection = dataSource.getConnection();
                    connection.setAutoCommit(false);
                    connections.add(connection);
                }
            }
            for (int iterationNumber = 0; iterationNumber < iterations.size(); iterationNumber++) {
                IterationWrapper<MySQLIteration> iterationWrapper = iterations.get(iterationNumber);
                MySQLIteration iterationConfigs = iterationWrapper.getIterationConfigs();
                for (int i = 0; i < iterationConfigs.getClients(); i++) {
                    Connection connection = connections.get(i);
                    Callable<ClientResult> task = getTask(connection, iterationConfigs, timeStart, false, iterationWrapper.getIterationModel());
                    clients.add(task);
                }
                List<Future<ClientResult>> resultsCandidates = executorService.invokeAll(clients);
                StepResult preresult = sendMetrics(resultsCandidates, iterationConfigs, iterationNumber, stepId, testConfigsWrapper.getTestExecutionModel(),
                        testConfigsWrapper.getTestingModeConfigs().testMode(), iterationWrapper.getIterationModel(), processCounter, iterationWrapper.getSavedQueryModel(), iterations.size());
                preresult.setConnections(connections);
                if (!preresult.getSuccess()){
                    return preresult;
                }
            }
        }
        return StepResult.builder()
                .message(null)
                .success(true)
                .stepId(stepId)
                .testExecutionId(testConfigsWrapper.getTestExecutionModel().getId())
                .userId(testConfigsWrapper.getTestExecutionModel().getUserId())
                .time(LocalDateTime.now())
                .testId(testConfigsWrapper.getTestModel().getId())
                .connections(connections)
                .build();
    }


    private Callable<ClientResult> getTask(Connection connection, MySQLIteration iterationConfigs, long timeStart, boolean closeConnection, IterationModel iterationModel) {
        return () -> {
            long timeStartWithoutConnection = System.currentTimeMillis();
            try {
                Statement statement = connection.createStatement();
                if (iterationConfigs.getTimeLimit() > 0) {
                    statement.setQueryTimeout(iterationConfigs.getTimeLimit());
                }
                timeStartWithoutConnection = System.currentTimeMillis();
                boolean isReturnable = statement.execute(iterationModel.getSql());
                connection.commit();
                long timeEndWithoutConnection = System.currentTimeMillis();
                ResultSet resultSet = isReturnable ? statement.getResultSet() : null;
                if (closeConnection) {
                    connection.close();
                }
                long timeEnd = System.currentTimeMillis();
                return ClientResult.builder()
                        .returnable(isReturnable)
                        .error(null)
                        .result(resultSet)
                        .success(true)
                        .timeEnd(timeEnd)
                        .timeEndWithoutConnection(timeEndWithoutConnection)
                        .timeStart(timeStart)
                        .timeStartWithoutConnection(timeStartWithoutConnection)
                        .build();
            } catch (Exception throwable) {
                long timeEndWithoutConnection = System.currentTimeMillis();
                long timeEnd = System.currentTimeMillis();
                return ClientResult.builder()
                        .success(false)
                        .error(throwable)
                        .returnable(false)
                        .result(null)
                        .timeStartWithoutConnection(timeStartWithoutConnection)
                        .timeStart(timeStart)
                        .timeEndWithoutConnection(timeEndWithoutConnection)
                        .timeEnd(timeEnd)
                        .build();
            }
        };
    }

    @SneakyThrows
    private <X extends TestMode.AbstractTestingModeConfig> StepResult sendMetrics(List<Future<ClientResult>> resultsCandidates, MySQLIteration iterationConfigs, int iterationNumber, int stepId, TestExecutionModel testExecutionModel,
                                                                                  TestMode testMode, IterationModel iterationModel, Function<ProcessModel, Integer> processCounter, SavedQueryModel savedQueryModel, int iterationSize) {
        long time = System.currentTimeMillis();
        boolean areAllCompleted = false;
        long timeLimit = iterationConfigs.getTimeLimit() != 0 ? iterationConfigs.getTimeLimit() : maxSqlExecutionTimeout;
        while (time + timeLimit * 1000 > System.currentTimeMillis()) {
            areAllCompleted = resultsCandidates
                    .stream()
                    .map(Future::isDone)
                    .reduce(true, (a, b) -> a && b);
            if (areAllCompleted) break;
        }
        if (areAllCompleted) {
            List<ClientResult> clientResults = new ArrayList<>();
            for (Future<ClientResult> clientResultFuture : resultsCandidates) {
                clientResults.add(clientResultFuture.get());
            }
            if (Objects.nonNull(savedQueryModel)) {
                for (ClientResult clientResult : clientResults) {
                    if (clientResult.getSuccess()) {
                        JSONArray result = WorkerUtils.resultSetToJson(clientResult.getResult());
                        JSONArray expected = new JSONArray(savedQueryModel.getResult());
                        JSONCompareResult compareResult = JSONCompare.compareJSON(expected, result, JSONCompareMode.LENIENT);
                        if (compareResult.failed()) {
                            clientResult.setSuccess(false);
                            clientResult.setError(new AssertionError(String.format("Expected %s; Actual: %s", WorkerUtils.cutString(savedQueryModel.getResult()), WorkerUtils.cutString(result.toString()))));
                        }
                    }
                }
            }
            Optional<ClientResult> firstException = clientResults
                    .stream()
                    .findFirst()
                    .filter(clientResult -> Objects.nonNull(clientResult.getError()));
            Float withConnections = Double.valueOf(clientResults
                    .stream()
                    .map(clientResult -> clientResult.getTimeEnd() - clientResult.getTimeStart())
                    .collect(Collectors.toList())
                    .stream()
                    .mapToLong(value -> value)
                    .average()
                    .orElse(Float.NaN))
                    .floatValue();
            Float withoutConnections = Double.valueOf(clientResults
                    .stream()
                    .map(clientResult -> clientResult.getTimeEndWithoutConnection() - clientResult.getTimeStartWithoutConnection())
                    .collect(Collectors.toList())
                    .stream()
                    .mapToLong(value -> value)
                    .average()
                    .orElse(Double.NaN))
                    .floatValue();
            MetricsModelWrapper metricsModelWrapper = MetricsModelWrapper.builder()
                    .testId(testExecutionModel.getTestConfigsId())
                    .clean(iterationModel.getClean())
                    .iterationId(iterationModel.getId())
                    .iterationNumber(iterationNumber + 1)
                    .stepId(stepId)
                    .success(firstException.isEmpty())
                    .testExecutionId(testExecutionModel.getId())
                    .time(LocalDateTime.now())
                    .userId(testExecutionModel.getUserId())
                    .process(firstException.isPresent() ? 100 : processCounter.apply(new ProcessModel(iterationSize, iterationNumber + 1)))
                    .testMode(testMode)
                    .withConnections(withConnections)
                    .withoutConnections(withoutConnections)
                    .workerId(workerCredential.getWorkerConfigurationModel().getId())
                    .iterationName(iterationModel.getName())
                    .message(firstException.map(clientResult -> clientResult.getError().getClass().getName() + ": " + clientResult.getError().getMessage()).orElse(null))
                    .build();
            socketClient.sendMessage(metricsModelWrapper, "logMetrics");
            return StepResult.fromMetricsModelWrapper(metricsModelWrapper);
        } else {
            MetricsModelWrapper metricsModelWrapper = MetricsModelWrapper.builder()
                    .testId(testExecutionModel.getTestConfigsId())
                    .clean(iterationModel.getClean())
                    .iterationId(iterationModel.getId())
                    .iterationNumber(iterationNumber + 1)
                    .stepId(stepId)
                    .success(false)
                    .process(100)
                    .testExecutionId(testExecutionModel.getId())
                    .time(LocalDateTime.now())
                    .userId(testExecutionModel.getUserId())
                    .testMode(testMode)
                    .message("Timeout: " + maxSqlExecutionTimeout)
                    .workerId(workerCredential.getWorkerConfigurationModel().getId())
                    .iterationName(iterationModel.getName())
                    .build();
            socketClient.sendMessage(metricsModelWrapper, "logMetrics");
            return StepResult.fromMetricsModelWrapper(metricsModelWrapper);
        }
    }
}

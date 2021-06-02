package com.razzzil.sphynx.worker.service.thread;

import com.razzzil.sphynx.commons.model.database.configs.DatabaseConfigs;
import com.razzzil.sphynx.commons.model.database.configs.MySQLConfigs;
import com.razzzil.sphynx.commons.model.database.configs.PostgreSQLConfigs;
import com.razzzil.sphynx.commons.model.iteration.configs.IterationConfigs;
import com.razzzil.sphynx.commons.model.iteration.configs.MySQLIteration;
import com.razzzil.sphynx.commons.model.iteration.configs.PostgreSQLIteration;
import com.razzzil.sphynx.commons.model.test.configs.MySQLTestConfigs;
import com.razzzil.sphynx.commons.model.test.configs.PostgreSQLTestConfigs;
import com.razzzil.sphynx.commons.model.test.configs.TestConfigs;
import com.razzzil.sphynx.commons.model.testmode.TestMode;
import com.razzzil.sphynx.commons.model.type.DatabaseType;
import com.razzzil.sphynx.commons.model.wrapper.TestUpdateStateWrapper;
import com.razzzil.sphynx.commons.model.wrapper.TestUserUpdateStateWrapper;
import com.razzzil.sphynx.worker.model.StepResult;
import com.razzzil.sphynx.worker.model.TestConfigsWrapper;
import com.razzzil.sphynx.worker.service.processors.testmode.TestModeProcessorInterface;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Consumer;

public class ProcessThread<T extends TestConfigs, V extends IterationConfigs, X extends TestMode.AbstractTestingModeConfig, Z extends DatabaseConfigs<? extends DataSource>> extends Thread {

    private ThreadEnv threadEnv;
    @Getter
    private final TestConfigsWrapper<T, V, X, Z> testConfigsWrapper;
    @Getter
    private TestUserUpdateStateWrapper testUserUpdateStateWrapper;

    private final Consumer<TestUpdateStateWrapper> endStep;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessThread.class);

    public ProcessThread(ThreadEnv threadEnv, TestConfigsWrapper<T, V, X, Z> testConfigsWrapper, Consumer<TestUpdateStateWrapper> endStep) {
        this.threadEnv = threadEnv;
        this.testConfigsWrapper = testConfigsWrapper;
        this.testUserUpdateStateWrapper = testConfigsWrapper.getUpdateWrapper();
        this.endStep = endStep;
    }

    private Long stoppedTime = null;
    @Getter
    private boolean paused = false;

    private StepResult process(TestConfigsWrapper<T, V, X, Z> testConfigsWrapper) {
        switch (testConfigsWrapper.getTestingModeConfigs().testMode()) {
            case TIMED: {
                return processByDatabase(testConfigsWrapper, (TestModeProcessorInterface<X>) threadEnv.getTimedTestModeProcessor());
            }
            case STEPPED: {
                return processByDatabase(testConfigsWrapper, (TestModeProcessorInterface<X>) threadEnv.getSteppedTestModeProcessor());
            }
            default:
                throw new IllegalArgumentException("Test Mode " + testConfigsWrapper.getTestingModeConfigs().testMode().name() + " is not supported");
        }
    }

    private StepResult processByDatabase(TestConfigsWrapper<T, V, X, Z> testConfigsWrapper, TestModeProcessorInterface<X> testModeProcessor) {
        DatabaseType databaseType = testConfigsWrapper.getDatabaseType();
        switch (databaseType) {
            case POSTGRESQL: {
                return testModeProcessor.process((TestConfigsWrapper<PostgreSQLTestConfigs, PostgreSQLIteration, X, PostgreSQLConfigs>) testConfigsWrapper, threadEnv.getPostgreSQLDatabaseProcessor());
            }
            case MYSQL: {
                return testModeProcessor.process((TestConfigsWrapper<MySQLTestConfigs, MySQLIteration, X, MySQLConfigs>) testConfigsWrapper, threadEnv.getMySQLDatabaseProcessor());
            }
            default:
                throw new IllegalArgumentException("Database type " + databaseType.name() + " is not supported");
        }
    }

    @Override
    public void run() {
        LOGGER.info("Started test: {}", testConfigsWrapper.getTestModel().getId());
        threadEnv.getSocketClient().sendMessage(testUserUpdateStateWrapper, "runTest");
        StepResult stepResult = process(testConfigsWrapper);
        if (Objects.nonNull(stepResult.getConnections()) && !stepResult.getConnections().isEmpty()) {
            stepResult.getConnections().forEach(connection -> {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
        LOGGER.info("Step result: {}", stepResult);
        if (!stepResult.getSuccess())
            testUserUpdateStateWrapper.setMessage(stepResult.getMessage());
        threadEnv.getSocketClient().sendMessage(testUserUpdateStateWrapper, stepResult.getSuccess() ? "finishTest" : "failedTest");
        endStep.accept(testUserUpdateStateWrapper);
    }

    public void pause() {
        if (!paused) {
            stoppedTime = System.currentTimeMillis();
            paused = true;
            threadEnv.getSocketClient().sendMessage(testUserUpdateStateWrapper, "pauseTest");
            suspend();
        } else throw new IllegalStateException("Thread has already paused");
    }

    public void unpause() {
        if (paused) {
            if (stoppedTime + threadEnv.getMaxOverdueTime() * 1000 > System.currentTimeMillis()) {
                stoppedTime = null;
                paused = false;
                threadEnv.getSocketClient().sendMessage(testUserUpdateStateWrapper, "runTest");
                resume();
            } else {
                threadEnv.getSocketClient().sendMessage(testUserUpdateStateWrapper, "overdueTest");
                endStep.accept(testUserUpdateStateWrapper);
                stop();
            }
        } else throw new IllegalStateException("Thread has already run");
    }

    public void kill() {
        threadEnv.getSocketClient().sendMessage(testUserUpdateStateWrapper, "terminateTest");
        stop();
    }


}

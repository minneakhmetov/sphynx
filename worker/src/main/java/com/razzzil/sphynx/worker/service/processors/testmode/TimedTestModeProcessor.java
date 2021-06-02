package com.razzzil.sphynx.worker.service.processors.testmode;

import com.razzzil.sphynx.commons.model.database.configs.DatabaseConfigs;
import com.razzzil.sphynx.commons.model.iteration.configs.IterationConfigs;
import com.razzzil.sphynx.commons.model.test.configs.TestConfigs;
import com.razzzil.sphynx.commons.model.testmode.TestMode;
import com.razzzil.sphynx.worker.model.ProcessModel;
import com.razzzil.sphynx.worker.model.StepIteration;
import com.razzzil.sphynx.worker.model.StepResult;
import com.razzzil.sphynx.worker.model.TestConfigsWrapper;
import com.razzzil.sphynx.worker.service.processors.database.DatabaseProcessorInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Service
@Scope("prototype")
public class TimedTestModeProcessor implements TestModeProcessorInterface<TestMode.TimedTestingModeConfig> {
    @Override
    public <T extends TestConfigs, V extends IterationConfigs, Z extends DatabaseConfigs<? extends DataSource>>
    StepResult process(TestConfigsWrapper<T, V, TestMode.TimedTestingModeConfig, Z> testConfigsWrapper, DatabaseProcessorInterface<T, V, Z> databaseProcessorInterface) {
        TestMode.TimedTestingModeConfig config = testConfigsWrapper.getTestingModeConfigs();
        final StepIteration stepIteration = new StepIteration(1, 0);
        long startTime = System.currentTimeMillis();
        long timeShift = startTime + config.getTimeLimit() * 1000;
        StepResult result = null;

        while (timeShift > System.currentTimeMillis()) {
            List<Connection> connections = null;
            if (Objects.nonNull(result)) {
                connections = result.getConnections();
            }
            result = databaseProcessorInterface.process(testConfigsWrapper, stepIteration.getStepId(), processModel -> {
                if (config.getRecursive()) {
                    long stepEnd = System.currentTimeMillis();
                    double iterationTime = (double) (stepEnd - startTime) / (double) stepIteration.getStepId();
                    double maxSteps = (double) (timeShift - startTime) / iterationTime;
                    double iter = (double) (processModel.getCurrentIteration() + stepIteration.getStepId()) / (double) (processModel.getIterationSize() + maxSteps);
                    return Math.min(100, stepIteration.incrementProcess((int) Math.round(iter * 100d)));
                } else {
                    double iteration = (double) processModel.getCurrentIteration() / (double) processModel.getIterationSize();
                    return Math.min(100, (int) Math.round(iteration * 100d));
                }
            }, connections);
            if (!result.getSuccess()) {
                return result;
            }
            if (!config.getRecursive()) {
                break;
            } else stepIteration.setStepId(stepIteration.getStepId() + 1);
        }
        long endTime = System.currentTimeMillis();
        float discrepancy = (float) (endTime - startTime) / 1000f;
        if (endTime - startTime > config.getTimeLimit() * 1000 && stepIteration.getStepId() <= 2 || Objects.isNull(result)) {
            return StepResult.builder()
                    .message(String.format("Time Limit overdue: Limited to %d, actual %f", config.getTimeLimit(), discrepancy))
                    .success(false)
                    .stepId(stepIteration.getStepId())
                    .testId(testConfigsWrapper.getTestModel().getId())
                    .testExecutionId(testConfigsWrapper.getTestExecutionModel().getId())
                    .userId(testConfigsWrapper.getTestExecutionModel().getUserId())
                    .time(LocalDateTime.now())
                    .build();
        } else {
            return result;
        }
    }
}

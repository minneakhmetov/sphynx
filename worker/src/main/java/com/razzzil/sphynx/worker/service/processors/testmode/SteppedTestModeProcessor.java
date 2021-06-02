package com.razzzil.sphynx.worker.service.processors.testmode;

import com.razzzil.sphynx.commons.model.database.configs.DatabaseConfigs;
import com.razzzil.sphynx.commons.model.iteration.configs.IterationConfigs;
import com.razzzil.sphynx.commons.model.test.configs.TestConfigs;
import com.razzzil.sphynx.commons.model.testmode.TestMode;
import com.razzzil.sphynx.worker.model.StepResult;
import com.razzzil.sphynx.worker.model.TestConfigsWrapper;
import com.razzzil.sphynx.worker.service.processors.database.DatabaseProcessorInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;

@Service
@Scope("prototype")
public class SteppedTestModeProcessor implements TestModeProcessorInterface<TestMode.SteppedTestingModeConfig> {
    @Override
    public <T extends TestConfigs, V extends IterationConfigs, Z extends DatabaseConfigs<? extends DataSource>>
    StepResult process(TestConfigsWrapper<T, V, TestMode.SteppedTestingModeConfig, Z> testConfigsWrapper, DatabaseProcessorInterface<T, V, Z> databaseProcessorInterface) {
        TestMode.SteppedTestingModeConfig configs = testConfigsWrapper.getTestingModeConfigs();
        StepResult result = null;
        for(int i = 1; i < configs.getSteps() + 1; i++){
            final int stepId = i;
            List<Connection> connections = null;
            if (Objects.nonNull(result)){
                connections = result.getConnections();
            }
            result = databaseProcessorInterface.process(testConfigsWrapper, i,
                    processModel -> Math.min(100, (int) Math.round((double) (stepId * 2 + processModel.getCurrentIteration()) / (double) (processModel.getIterationSize() + configs.getSteps() * 2) * 100d)), connections);
            if (!result.getSuccess()){
                return result;
            }
        }
        return result;
    }
}

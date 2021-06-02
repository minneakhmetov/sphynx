package com.razzzil.sphynx.worker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.razzzil.sphynx.commons.model.database.DatabaseModel;
import com.razzzil.sphynx.commons.model.database.configs.DatabaseConfigs;
import com.razzzil.sphynx.commons.model.execution.TestExecutionModel;
import com.razzzil.sphynx.commons.model.iteration.configs.IterationConfigs;
import com.razzzil.sphynx.commons.model.test.TestModel;
import com.razzzil.sphynx.commons.model.test.configs.TestConfigs;
import com.razzzil.sphynx.commons.model.testmode.TestMode;
import com.razzzil.sphynx.commons.model.type.DatabaseType;
import com.razzzil.sphynx.commons.model.wrapper.TestIterationModel;
import com.razzzil.sphynx.commons.model.wrapper.TestStartWrapper;
import com.razzzil.sphynx.commons.model.wrapper.TestUserUpdateStateWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TestConfigsWrapper<T extends TestConfigs, V extends IterationConfigs, X extends TestMode.AbstractTestingModeConfig, Z extends DatabaseConfigs<? extends DataSource>> {
    private TestExecutionModel testExecutionModel;
    private DatabaseModel databaseModel;
    private TestModel testModel;
    private Z databaseConfigs;
    private T testConfigs;
    private X testingModeConfigs;
    private List<IterationWrapper<V>> iterations;
    private DatabaseType databaseType;
    private Integer workerId;

    public TestConfigsWrapper(TestStartWrapper testStartWrapper, Integer workerId){
        this.testExecutionModel = testStartWrapper.getTestExecutionModel();
        this.databaseModel = testStartWrapper.getDatabaseModel();
        this.testModel = testStartWrapper.getTestIterationModel().getTest();
        this.databaseConfigs = databaseModel.deserialize();
        this.testConfigs = testModel.deserializeConfigs();
        this.testingModeConfigs = testModel.deserializeTestModeConfigs();
        this.databaseType = databaseModel.getType();
        this.workerId = workerId;
        this.iterations = testStartWrapper
                .getTestIterationModel()
                .getIterations()
                .stream()
                .map(wrapper -> new IterationWrapper<V>(wrapper.getIterationModel(), wrapper.getSavedQueryModel(), databaseType))
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public TestUserUpdateStateWrapper getUpdateWrapper(){
        return TestUserUpdateStateWrapper.builder()
                .id(testExecutionModel.getId())
                .userId(testExecutionModel.getUserId())
                .workerId(workerId)
                .build();
    }
}

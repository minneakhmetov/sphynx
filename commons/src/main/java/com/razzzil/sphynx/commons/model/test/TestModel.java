package com.razzzil.sphynx.commons.model.test;

import com.razzzil.sphynx.commons.model.test.configs.TestConfigs;
import com.razzzil.sphynx.commons.model.test.state.TestState;
import com.razzzil.sphynx.commons.model.testmode.TestMode;
import com.razzzil.sphynx.commons.model.type.DatabaseType;
import com.razzzil.sphynx.commons.model.webhook.WebhookModel;
import com.razzzil.sphynx.commons.model.worker.WorkerModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.razzzil.sphynx.commons.constant.StaticsConstants.OM;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TestModel {
    private Integer id;
    private Integer userId;
    private Integer databaseId;
    private Integer workerId;
    private String name;
    private DatabaseType databaseType;
    private String configs;
    private TestMode testMode;
    private String testModeConfigs;
    private TestState state;
    private Integer lastExecutionId;
    private Integer process;
    private String cron;
    private LocalDateTime nextExecutionTime;
    private List<WebhookModel> webhookModels;

    public <T extends TestConfigs, V extends TestMode.AbstractTestingModeConfig> TestModel(Integer id, Integer userId, Integer databaseId, Integer workerId, String name, DatabaseType databaseType, T config, TestMode testMode, V testModeConfigs, TestState testState, Integer lastExecutionId, Integer process) {
        this.id = id;
        this.userId = userId;
        this.databaseId = databaseId;
        this.name = name;
        this.databaseType = databaseType;
        this.testMode = testMode;
        this.workerId = workerId;
        this.state = testState;
        this.lastExecutionId = lastExecutionId;
        this.process = process;
        setTestModeConfigsByConfigs(testModeConfigs);
        setConfigsAsObject(config);
    }

    @SneakyThrows
    public <T extends TestConfigs> T deserializeConfigs(Class<T> castClass) {
        return OM.readValue(configs, castClass);
    }

    @SneakyThrows
    public <T extends TestConfigs> T deserializeConfigs() {
        return OM.readValue(configs, databaseType.getTestConfigsTypeReference());
    }

    @SneakyThrows
    public static <T extends TestConfigs> T deserializeConfigs(String configs, DatabaseType type) {
        return OM.readValue(configs, type.getTestConfigsTypeReference());
    }

//    @SneakyThrows
//    public <T extends TestConfigs> String serializeConfigs(T object) {
//        if (Objects.nonNull(object)) {
//            setDatabaseType(object.databaseType());
//            return OM.writeValueAsString(object);
//        } else return null;
//    }

    @SneakyThrows
    public <T extends TestConfigs> void setConfigsAsObject(T object) {
        if (Objects.nonNull(object)) {
            setDatabaseType(object.databaseType());
            configs = OM.writeValueAsString(object);
        }
    }

    public static TestModelBuilder builder() {
        return new TestModelBuilder();
    }

    @SneakyThrows
    public <T extends TestMode.AbstractTestingModeConfig> T deserializeTestModeConfigs(Class<T> castClass) {
        return OM.readValue(testModeConfigs, castClass);
    }

    @SneakyThrows
    public <T extends TestMode.AbstractTestingModeConfig> T deserializeTestModeConfigs() {
        return OM.readValue(testModeConfigs, testMode.getTypeReference());
    }

    @SneakyThrows
    public static <T extends TestMode.AbstractTestingModeConfig> T deserializeTestModeConfigs(String configs, TestMode testMode) {
        return OM.readValue(configs, testMode.getTypeReference());
    }

//    @SneakyThrows
//    public <T extends TestMode.AbstractTestingModeConfig> String serializeTestModeConfigs(T object) {
//        if (Objects.nonNull(object)) {
//            setTestMode(object.testMode());
//            return OM.writeValueAsString(object);
//        } else return null;
//    }

    @SneakyThrows
    public <T extends TestMode.AbstractTestingModeConfig> void setTestModeConfigsByConfigs(T object) {
        if (Objects.nonNull(object)) {
            setTestMode(object.testMode());
            testModeConfigs = OM.writeValueAsString(object);
        }
    }

    public static final class TestModelBuilder {
        private TestModel testModel;

        private TestModelBuilder() {
            testModel = new TestModel();
        }

        public TestModelBuilder id(Integer id) {
            testModel.setId(id);
            return this;
        }

        public TestModelBuilder userId(Integer userId) {
            testModel.setUserId(userId);
            return this;
        }

        public TestModelBuilder databaseId(Integer databaseId) {
            testModel.setDatabaseId(databaseId);
            return this;
        }

        public TestModelBuilder workerId(Integer workerId) {
            testModel.setWorkerId(workerId);
            return this;
        }

        public TestModelBuilder name(String name) {
            testModel.setName(name);
            return this;
        }

        public <T extends TestConfigs> TestModelBuilder configs(T object) {
            testModel.setConfigsAsObject(object);
            return this;
        }
        public TestModelBuilder configs(String object, DatabaseType databaseType) {
            testModel.setConfigs(object);
            testModel.setDatabaseType(databaseType);
            return this;
        }

        public <T extends TestMode.AbstractTestingModeConfig> TestModelBuilder testModeConfigs(T object) {
            testModel.setTestModeConfigsByConfigs(object);
            return this;
        }

        public TestModelBuilder testModeConfigs(String object, TestMode testMode) {
            testModel.setTestModeConfigs(object);
            testModel.setTestMode(testMode);
            return this;
        }

        public TestModelBuilder testState(TestState testState) {
            testModel.setState(testState);
            return this;
        }

        public TestModelBuilder lastExecutionId(Integer lastExecutionId) {
            testModel.setLastExecutionId(lastExecutionId);
            return this;
        }

        public TestModelBuilder process(Integer process) {
            testModel.setProcess(process);
            return this;
        }

        public TestModelBuilder nextExecutionTime(LocalDateTime localDateTime){
            testModel.setNextExecutionTime(localDateTime);
            return this;
        }

        public TestModelBuilder cron(String cron){
            testModel.setCron(cron);
            return this;
        }

        public TestModel build() {
            return testModel;
        }
    }
}

package com.razzzil.sphynx.commons.model.iteration;

import com.razzzil.sphynx.commons.model.type.DatabaseType;
import com.razzzil.sphynx.commons.model.iteration.configs.IterationConfigs;
import com.razzzil.sphynx.commons.constant.StaticsConstants;
import lombok.*;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class IterationModel {
    private Integer id;
    private Integer testConfigsId;
    private Integer savedQueryId;
    private Integer userId;
    private Integer databaseId;
    private String configs;
    private String name;
    private String sql;
    private Boolean clean;

    public <T extends IterationConfigs> IterationModel(Integer id, Integer testConfigsId, T config) {
        this.id = id;
        this.testConfigsId = testConfigsId;
        setConfigsAsObject(config);
    }

    @SneakyThrows
    public <T extends IterationConfigs> T deserialize(Class<T> castClass) {
        return StaticsConstants.OM.readValue(configs, castClass);
    }

    @SneakyThrows
    public <T extends IterationConfigs> T deserialize(DatabaseType databaseType) {
        return StaticsConstants.OM.readValue(configs, databaseType.getIterationConfigsTypeReference());
    }

    @SneakyThrows
    public static <T extends IterationConfigs> T deserialize(String configs, DatabaseType type) {
        return StaticsConstants.OM.readValue(configs, type.getIterationConfigsTypeReference());
    }

//    @SneakyThrows
//    public <T extends IterationConfigs> String serialize(T object) {
//        if (Objects.nonNull(object)) {
//            return OM.writeValueAsString(object);
//        } else return null;
//    }

    @SneakyThrows
    public <T extends IterationConfigs> void setConfigsAsObject(T object) {
        if (Objects.nonNull(object)) {
             configs = StaticsConstants.OM.writeValueAsString(object);
        }
    }

    public static IterationModelBuilder builder() {
        return new IterationModelBuilder();
    }

    public static final class IterationModelBuilder {
        private IterationModel iterationModel;

        private IterationModelBuilder() {
            iterationModel = new IterationModel();
        }

        public IterationModelBuilder id(Integer id) {
            iterationModel.setId(id);
            return this;
        }

        public IterationModelBuilder testConfigId(Integer testConfigId) {
            iterationModel.setTestConfigsId(testConfigId);
            return this;
        }

        public IterationModelBuilder userId(Integer userId) {
            iterationModel.setUserId(userId);
            return this;
        }

        public IterationModelBuilder savedQueryId(Integer savedQueryId) {
            iterationModel.setSavedQueryId(savedQueryId);
            return this;
        }


        public IterationModelBuilder databaseId(Integer databaseId) {
            iterationModel.setDatabaseId(databaseId);
            return this;
        }

        public <T extends IterationConfigs> IterationModelBuilder configs(T object){
            iterationModel.setConfigsAsObject(object);
            return this;
        }

        public <T extends IterationConfigs> IterationModelBuilder name(String name){
            iterationModel.setName(name);
            return this;
        }

        public <T extends IterationConfigs> IterationModelBuilder sql(String sql){
            iterationModel.setSql(sql);
            return this;
        }

        public <T extends IterationConfigs> IterationModelBuilder clean(Boolean clean){
            iterationModel.setClean(clean);
            return this;
        }

        public IterationModel build() {
            return iterationModel;
        }
    }
}

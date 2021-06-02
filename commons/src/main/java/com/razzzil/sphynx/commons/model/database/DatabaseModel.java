package com.razzzil.sphynx.commons.model.database;


import com.razzzil.sphynx.commons.model.database.configs.DatabaseConfigs;
import com.razzzil.sphynx.commons.model.type.DatabaseType;
import com.razzzil.sphynx.commons.model.worker.connectionstate.ConnectionState;
import lombok.*;

import javax.sql.DataSource;

import java.util.Objects;

import static com.razzzil.sphynx.commons.constant.StaticsConstants.OM;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DatabaseModel {

    private Integer id;
    private Integer userId;
    private DatabaseType type;
    private String alias;
    private Integer workerId;
    private String configs;
    private ConnectionState connectionState;

    public <T extends DatabaseConfigs<? extends DataSource>> DatabaseModel(Integer id, Integer userId, T config) {
        this.id = id;
        this.userId = userId;
        setConfigsAsObject(config);
    }

    @SneakyThrows
    public <T extends DatabaseConfigs<? extends DataSource>> T deserialize(Class<T> castClass) {
        return OM.readValue(configs, castClass);
    }

    @SneakyThrows
    public <T extends DatabaseConfigs<? extends DataSource>> T deserialize() {
        return OM.readValue(configs, type.getDatabaseConfigsTypeReference());
    }

    @SneakyThrows
    public static <T extends DatabaseConfigs<? extends DataSource>> T deserialize(String configs, DatabaseType type) {
        return OM.readValue(configs, type.getDatabaseConfigsTypeReference());
    }

//    @SneakyThrows
//    public <T extends DatabaseConfigs<? extends DataSource>> String serialize(T object) {
//        if (Objects.nonNull(object)) {
//            setType(object.type());
//            return OM.writeValueAsString(object);
//        } else return null;
//    }

    @SneakyThrows
    public <T extends DatabaseConfigs<? extends DataSource>> void setConfigsAsObject(T object) {
        if (Objects.nonNull(object)) {
            setType(object.type());
            configs = OM.writeValueAsString(object);
        }
    }

    public static DatabaseModelBuilder builder() {
        return new DatabaseModelBuilder();
    }

    public static final class DatabaseModelBuilder {
        private DatabaseModel databaseModel;

        private DatabaseModelBuilder() {
            databaseModel = new DatabaseModel();
        }

        public DatabaseModelBuilder id(Integer id) {
            databaseModel.setId(id);
            return this;
        }

        public DatabaseModelBuilder userId(Integer userId) {
            databaseModel.setUserId(userId);
            return this;
        }

        public DatabaseModelBuilder workerId(Integer workerId) {
            databaseModel.setWorkerId(workerId);
            return this;
        }

        public <T extends DatabaseConfigs<? extends DataSource>> DatabaseModelBuilder configs(T configs) {
            databaseModel.setConfigsAsObject(configs);
            return this;
        }

        public DatabaseModelBuilder alias(String alias) {
            databaseModel.setAlias(alias);
            return this;
        }

        public DatabaseModelBuilder connectionState(ConnectionState connectionState){
            databaseModel.setConnectionState(connectionState);
            return this;
        }

        public DatabaseModel build() {
            return databaseModel;
        }
    }
}

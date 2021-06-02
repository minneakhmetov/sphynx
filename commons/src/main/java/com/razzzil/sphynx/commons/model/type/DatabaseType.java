package com.razzzil.sphynx.commons.model.type;

import com.fasterxml.jackson.core.type.TypeReference;
import com.razzzil.sphynx.commons.model.database.configs.DatabaseConfigs;
import com.razzzil.sphynx.commons.model.database.configs.MySQLConfigs;
import com.razzzil.sphynx.commons.model.database.configs.PostgreSQLConfigs;
import com.razzzil.sphynx.commons.model.iteration.configs.IterationConfigs;
import com.razzzil.sphynx.commons.model.iteration.configs.MySQLIteration;
import com.razzzil.sphynx.commons.model.iteration.configs.PostgreSQLIteration;
import com.razzzil.sphynx.commons.model.test.configs.MySQLTestConfigs;
import com.razzzil.sphynx.commons.model.test.configs.PostgreSQLTestConfigs;
import com.razzzil.sphynx.commons.model.test.configs.TestConfigs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.jdbc.DatabaseDriver;

import javax.sql.DataSource;
import java.lang.reflect.Type;

@AllArgsConstructor
public enum DatabaseType {
    POSTGRESQL(PostgreSQLConfigs.class, PostgreSQLIteration.class, PostgreSQLTestConfigs.class, DatabaseDriver.POSTGRESQL),
    MYSQL(MySQLConfigs.class, MySQLIteration.class, MySQLTestConfigs.class, DatabaseDriver.MYSQL);

    @Getter
    private Class<? extends DatabaseConfigs<? extends DataSource>> databaseConfigsTypeReference;

    @Getter
    private Class<? extends IterationConfigs> iterationConfigsTypeReference;

    @Getter
    private Class<? extends TestConfigs> testConfigsTypeReference;

    @Getter
    private DatabaseDriver databaseDriver;

    public <T extends DatabaseConfigs<? extends DataSource>> TypeReference<T> getDatabaseConfigsTypeReference(){
        return new TypeReference<>() {
            @Override
            public Type getType() {
                return databaseConfigsTypeReference;
            }
        };
    }

    public <T extends IterationConfigs> TypeReference<T> getIterationConfigsTypeReference(){
        return new TypeReference<>() {
            @Override
            public Type getType() {
                return iterationConfigsTypeReference;
            }
        };
    }

    public <T extends TestConfigs> TypeReference<T> getTestConfigsTypeReference(){
        return new TypeReference<>() {
            @Override
            public Type getType() {
                return testConfigsTypeReference;
            }
        };
    }

    public static DatabaseType get(String value){
        for (DatabaseType databaseType: values()){
            if (databaseType.name().equals(value))
                return databaseType;
        }
        throw new IllegalArgumentException("Database Type " + value + " is not supported");
    }




}

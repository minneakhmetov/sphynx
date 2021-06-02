package com.razzzil.sphynx.commons.model.database.configs;

import com.razzzil.sphynx.commons.constant.ValidationPresets;
import com.razzzil.sphynx.commons.model.type.DatabaseType;
import com.razzzil.sphynx.commons.validation.ValidationResult;
import lombok.*;
import org.postgresql.ds.PGSimpleDataSource;

import java.util.Objects;
import java.util.Stack;
import java.util.regex.Matcher;

import static com.razzzil.sphynx.commons.constant.Regex.*;
import static com.razzzil.sphynx.commons.constant.Regex.VALID_PORT_NUMBER;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PostgreSQLConfigs extends DatabaseConfigs<PGSimpleDataSource> {
    private String host;
    private Integer port;
    private String database;
    private String user;
    private String password;

    @AllArgsConstructor
    @Getter
    public enum Fields {
        HOST("host"),
        PORT("port"),
        DATABASE("database"),
        USER("user"),
        PASSWORD("password");
        private String name;
    }

    @Override
    public PGSimpleDataSource datasource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setPortNumber(port);
        dataSource.setDatabaseName(database);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setServerName(host);
        return dataSource;
    }

    @Override
    public DatabaseType type() {
        return DatabaseType.POSTGRESQL;
    }

    @Override
    public Stack<ValidationResult> validate() {
        Stack<ValidationResult> validationResults = new Stack<>();
        if (Objects.isNull(host)){
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.HOST.getName())
                    .description(ValidationPresets.formatIsNull(Fields.HOST.getName()))
                    .build());
        } else {
            Matcher hostDomainMatcher = VALID_HOST_REGEX.matcher(host);
            Matcher hostIpv4Matcher = VALID_IPV4_ADDRESS_REGEX.matcher(host);
            Matcher hostIpv6Matcher = VALID_IPV6_ADDRESS_REGEX.matcher(host);
            if (!hostDomainMatcher.find() && !hostIpv4Matcher.find() && !hostIpv6Matcher.find()) {
                validationResults.add(ValidationResult.builder()
                        .fieldName(Fields.HOST.getName())
                        .description(ValidationPresets.formatIsIncorrect(Fields.HOST.getName()))
                        .build());
            }
        }
        if (Objects.isNull(port)){
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.PORT.getName())
                    .description(ValidationPresets.formatIsNull(Fields.PORT.getName()))
                    .build());
        } else if (!VALID_PORT_NUMBER.matcher(String.valueOf(port)).find()){
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.PORT.getName())
                    .description(ValidationPresets.formatIsIncorrect(Fields.PORT.getName()))
                    .build());
        }
        if (Objects.isNull(database)){
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.DATABASE.getName())
                    .description(ValidationPresets.formatIsIncorrect(Fields.DATABASE.getName()))
                    .build());
        }
        if (Objects.isNull(user)){
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.USER.getName())
                    .description(ValidationPresets.formatIsIncorrect(Fields.USER.getName()))
                    .build());
        }
        if (Objects.isNull(password)){
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.PASSWORD.getName())
                    .description(ValidationPresets.formatIsIncorrect(Fields.PASSWORD.getName()))
                    .build());
        }
        return validationResults;
    }
}

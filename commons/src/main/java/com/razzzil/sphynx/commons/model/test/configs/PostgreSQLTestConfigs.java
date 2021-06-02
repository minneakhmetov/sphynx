package com.razzzil.sphynx.commons.model.test.configs;

import com.razzzil.sphynx.commons.constant.ValidationPresets;
import com.razzzil.sphynx.commons.model.type.DatabaseType;
import com.razzzil.sphynx.commons.validation.ValidationResult;
import lombok.*;

import java.util.Objects;
import java.util.Stack;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PostgreSQLTestConfigs extends TestConfigs {
//    private Integer clients;
    private Boolean connect;
    private Boolean vacuum;

//    private String scaleIncrement;
//    private String clientsIncrement;
//    private String timeLimitIncrement;

    @Override
    public DatabaseType databaseType() {
        return DatabaseType.POSTGRESQL;
    }

    @AllArgsConstructor
    @Getter
    public enum Fields {
//        CLIENTS("clients"),
        CONNECT("connect"),
        VACUUM("vacuum"),
//        SCALE_INCREMENT("scaleIncrement"),
//        CLIENTS_INCREMENT("clientsIncrement"),
//        TIME_LIMIT_INCREMENT("timeLimitIncrement");
        ;
        private String name;
    }

    @Override
    public Stack<ValidationResult> validate() {
        Stack<ValidationResult> validationResults = new Stack<>();
//        if (Objects.isNull(clients)) {
//            validationResults.add(ValidationResult.builder()
//                    .fieldName(Fields.CLIENTS.getName())
//                    .description(StaticsConstants.ValidationPresets.formatIsNull(Fields.CLIENTS.getName()))
//                    .build());
//        } else if (clients < 0 || clients > StaticsConstants.MaxConstants.MAX_CLIENTS) {
//            validationResults.add(ValidationResult.builder()
//                    .fieldName(Fields.CLIENTS.getName())
//                    .description(StaticsConstants.ValidationPresets.formatCustom("%s must be greater than 0 and less than %d", Fields.CLIENTS.getName(), StaticsConstants.MaxConstants.MAX_CLIENTS))
//                    .build());
//        }
        if (Objects.isNull(connect)) {
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.CONNECT.getName())
                    .description(ValidationPresets.formatIsNull(Fields.CONNECT.getName()))
                    .build());
        }
        if (Objects.isNull(vacuum)) {
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.VACUUM.getName())
                    .description(ValidationPresets.formatIsNull(Fields.VACUUM.getName()))
                    .build());
        }
//        if (Objects.isNull(scaleIncrement)) {
//            validationResults.add(ValidationResult.builder()
//                    .fieldName(Fields.SCALE_INCREMENT.getName())
//                    .description(StaticsConstants.ValidationPresets.formatIsNull(Fields.SCALE_INCREMENT.getName()))
//                    .build());
//        } else {
//            Matcher scaleIncrementMatcher = VALID_INCREMENT_REGEX.matcher(scaleIncrement);
//            if (!scaleIncrementMatcher.find()) {
//                validationResults.add(ValidationResult.builder()
//                        .fieldName(Fields.SCALE_INCREMENT.getName())
//                        .description(StaticsConstants.ValidationPresets.formatIsIncorrect(Fields.SCALE_INCREMENT.getName()))
//                        .build());
//            }
//        }
//        if (Objects.isNull(clientsIncrement)) {
//            validationResults.add(ValidationResult.builder()
//                    .fieldName(Fields.CLIENTS_INCREMENT.getName())
//                    .description(StaticsConstants.ValidationPresets.formatIsNull(Fields.CLIENTS_INCREMENT.getName()))
//                    .build());
//        } else {
//            Matcher clientsIncrementMatcher = VALID_INCREMENT_REGEX.matcher(clientsIncrement);
//            if (!clientsIncrementMatcher.find()) {
//                validationResults.add(ValidationResult.builder()
//                        .fieldName(Fields.CLIENTS_INCREMENT.getName())
//                        .description(StaticsConstants.ValidationPresets.formatIsIncorrect(Fields.CLIENTS_INCREMENT.getName()))
//                        .build());
//            }
//        }
//        if (Objects.isNull(timeLimitIncrement)) {
//            validationResults.add(ValidationResult.builder()
//                    .fieldName(Fields.TIME_LIMIT_INCREMENT.getName())
//                    .description(StaticsConstants.ValidationPresets.formatIsNull(Fields.TIME_LIMIT_INCREMENT.getName()))
//                    .build());
//        } else {
//            Matcher timeLimitIncrementMatcher = VALID_INCREMENT_REGEX.matcher(timeLimitIncrement);
//            if (!timeLimitIncrementMatcher.find()) {
//                validationResults.add(ValidationResult.builder()
//                        .fieldName(Fields.TIME_LIMIT_INCREMENT.getName())
//                        .description(StaticsConstants.ValidationPresets.formatIsIncorrect(Fields.TIME_LIMIT_INCREMENT.getName()))
//                        .build());
//            }
//        }
        return validationResults;
    }

}

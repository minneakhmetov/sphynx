package com.razzzil.sphynx.commons.model.iteration.configs;

import com.razzzil.sphynx.commons.constant.ValidationPresets;
import com.razzzil.sphynx.commons.model.type.DatabaseType;
import com.razzzil.sphynx.commons.validation.ValidationResult;
import com.razzzil.sphynx.commons.constant.MaximumConstants;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Objects;
import java.util.Stack;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MySQLIteration extends IterationConfigs {

    private Integer clients;
    private Integer timeLimit;
//    private Integer scale;
//    private Boolean transactional;

    @Override
    public DatabaseType databaseType() {
        return DatabaseType.MYSQL;
    }

    @AllArgsConstructor
    @Getter
    public enum Fields {
        CLIENTS("clients"),
        TIME_LIMIT("timeLimit"),
//        SCALE("scale"),
//        TRANSACTIONAL("transactional")
        ;
        private String name;
    }

    @Override
    public Stack<ValidationResult> validate() {
        Stack<ValidationResult> validationResults = new Stack<>();
        if (Objects.isNull(clients)){
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.CLIENTS.getName())
                    .description(ValidationPresets.formatIsNull(Fields.CLIENTS.getName()))
                    .build());
        } else if (clients < 0 || clients > MaximumConstants.MAX_CLIENTS){
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.CLIENTS.getName())
                    .description(ValidationPresets.formatCustom("%s must be greater than 0 and less than %d", Fields.CLIENTS.getName(), MaximumConstants.MAX_CLIENTS))
                    .build());
        }
        if (Objects.isNull(timeLimit)){
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.TIME_LIMIT.getName())
                    .description(ValidationPresets.formatIsNull(Fields.TIME_LIMIT.getName()))
                    .build());
        } else if (timeLimit < 0 || timeLimit > MaximumConstants.MAX_TIME_LIMIT){
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.TIME_LIMIT.getName())
                    .description(ValidationPresets.formatCustom("%s must be greater than 0 and less than %d", Fields.TIME_LIMIT.getName(), MaximumConstants.MAX_TIME_LIMIT))
                    .build());
        }
//        if (Objects.isNull(scale)){
//            validationResults.add(ValidationResult.builder()
//                    .fieldName(Fields.SCALE.getName())
//                    .description(ValidationPresets.formatIsNull(Fields.SCALE.getName()))
//                    .build());
//        } else if (scale < 0 || scale > MaximumConstants.MAX_SCALE_ITERATION){
//            validationResults.add(ValidationResult.builder()
//                    .fieldName(Fields.SCALE.getName())
//                    .description(ValidationPresets.formatCustom("%s must be greater than 0 and less than %d",Fields.SCALE.getName(), MaximumConstants.MAX_SCALE_ITERATION))
//                    .build());
//        }
//        if (Objects.isNull(transactional)){
//            validationResults.add(ValidationResult.builder()
//                    .fieldName(Fields.TRANSACTIONAL.getName())
//                    .description(ValidationPresets.formatIsNull(Fields.TRANSACTIONAL.getName()))
//                    .build());
//        }
        return validationResults;
    }


}

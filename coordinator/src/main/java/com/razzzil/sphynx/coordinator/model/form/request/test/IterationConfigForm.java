package com.razzzil.sphynx.coordinator.model.form.request.test;

import com.razzzil.sphynx.commons.constant.ValidationPresets;
import com.razzzil.sphynx.commons.model.iteration.configs.IterationConfigs;
import com.razzzil.sphynx.commons.validation.Validated;
import com.razzzil.sphynx.commons.validation.ValidationResult;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Objects;
import java.util.Stack;

import static com.razzzil.sphynx.commons.constant.MaximumConstants.MAX_NUMBER_ITERATION;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class IterationConfigForm implements Validated {
    private String name;
    private String sql;
    private Boolean clean;
    private String configs;
    private Integer savedQueryId;

    @AllArgsConstructor
    @Getter
    public enum Fields {
        SQL("sql"),
        NAME("name"),
        SAVED_QUERY_ID("savedQueryId"),
        CLEAN("clean");
        private String name;
    }

    @Override
    public Stack<ValidationResult> validate() {
        Stack<ValidationResult> validationResults = new Stack<>();
        if (Objects.isNull(clean)) {
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.CLEAN.getName())
                    .description(ValidationPresets.formatIsNull(Fields.CLEAN.getName()))
                    .build());
        }
        if (Objects.isNull(sql)){
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.SQL.getName())
                    .description(ValidationPresets.formatIsNull(Fields.SQL.getName()))
                    .build());
        }
        if (Objects.isNull(name)){
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.NAME.getName())
                    .description(ValidationPresets.formatIsNull(Fields.NAME.getName()))
                    .build());
        }
        if (Objects.isNull(savedQueryId)){
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.SAVED_QUERY_ID.getName())
                    .description(ValidationPresets.formatIsNull(Fields.SAVED_QUERY_ID.getName()))
                    .build());
        }
        return validationResults;
    }
}

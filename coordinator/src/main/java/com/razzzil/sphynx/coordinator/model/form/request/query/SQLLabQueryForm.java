package com.razzzil.sphynx.coordinator.model.form.request.query;

import com.razzzil.sphynx.commons.constant.ValidationPresets;
import com.razzzil.sphynx.commons.validation.Validated;
import com.razzzil.sphynx.commons.validation.ValidationResult;
import com.razzzil.sphynx.coordinator.model.form.request.test.IterationConfigForm;
import lombok.*;

import java.util.Objects;
import java.util.Stack;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SQLLabQueryForm implements Validated {
    private Integer databaseId;
    private String sql;

    @AllArgsConstructor
    @Getter
    public enum Fields {
        DATABASE_ID("databaseId"),
        SQL("sql");
        private String name;
    }

    @Override
    public Stack<ValidationResult> validate() {
        Stack<ValidationResult> validationResults = new Stack<>();
        if (Objects.isNull(sql)){
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.SQL.getName())
                    .description(ValidationPresets.formatIsNull(Fields.SQL.getName()))
                    .build());
        }
        if (Objects.isNull(databaseId)){
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.DATABASE_ID.getName())
                    .description(ValidationPresets.formatIsNull(Fields.DATABASE_ID.getName()))
                    .build());
        }
        return validationResults;
    }
}

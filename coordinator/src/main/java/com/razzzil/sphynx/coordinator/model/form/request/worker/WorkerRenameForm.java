package com.razzzil.sphynx.coordinator.model.form.request.worker;

import com.razzzil.sphynx.commons.constant.ValidationPresets;
import com.razzzil.sphynx.commons.validation.Validated;
import com.razzzil.sphynx.commons.validation.ValidationResult;
import lombok.*;

import java.util.Objects;
import java.util.Stack;

import static com.razzzil.sphynx.commons.constant.Regex.VALID_LOGIN_REGEX;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WorkerRenameForm implements Validated {
    private Integer id;
    private String alias;

    @Getter
    @AllArgsConstructor
    private enum  Fields {
        ID("id"),
        ALIAS("alias");
        private String name;
    }

    public Stack<ValidationResult> validate(){
        Stack<ValidationResult> validationResults = new Stack<>();
        if (Objects.isNull(id)) {
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.ID.getName())
                    .description(ValidationPresets.formatIsNull(Fields.ID.getName()))
                    .build());
        }
        if (Objects.isNull(alias)) {
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.ALIAS.getName())
                    .description(ValidationPresets.formatIsNull(Fields.ALIAS.getName()))
                    .build());
        } else if (!VALID_LOGIN_REGEX.matcher(alias).find()) {
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.ALIAS.getName())
                    .description(ValidationPresets.formatIsIncorrect(Fields.ALIAS.getName()))
                    .build());
        }
        return validationResults;
    }
}

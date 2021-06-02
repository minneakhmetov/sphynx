package com.razzzil.sphynx.coordinator.model.form.request.worker;

import com.razzzil.sphynx.commons.constant.ValidationPresets;
import com.razzzil.sphynx.commons.validation.Validated;
import com.razzzil.sphynx.commons.validation.ValidationResult;
import lombok.*;

import java.util.Objects;
import java.util.Stack;

import static com.razzzil.sphynx.commons.constant.Regex.VALID_LOGIN_REGEX;
import static com.razzzil.sphynx.commons.constant.Regex.VALID_VERSION_REGEX;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WorkerForm implements Validated {
    private String alias;
    private String version;

    @Getter
    @AllArgsConstructor
    private enum  Fields {
        ALIAS("alias"),
        VERSION("version");
        private String name;
    }

    public Stack<ValidationResult> validate(){
        Stack<ValidationResult> validationResults = new Stack<>();
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
        if (Objects.isNull(version)) {
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.VERSION.getName())
                    .description(ValidationPresets.formatIsNull(Fields.VERSION.getName()))
                    .build());
        } else if (!VALID_VERSION_REGEX.matcher(version).find()){
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.VERSION.getName())
                    .description(ValidationPresets.formatIsIncorrect(Fields.VERSION.getName()))
                    .build());
        }
        return validationResults;
    }
}

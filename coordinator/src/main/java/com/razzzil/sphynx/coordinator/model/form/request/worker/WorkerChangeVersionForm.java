package com.razzzil.sphynx.coordinator.model.form.request.worker;

import com.razzzil.sphynx.commons.constant.ValidationPresets;
import com.razzzil.sphynx.commons.validation.Validated;
import com.razzzil.sphynx.commons.validation.ValidationResult;
import lombok.*;

import java.util.Objects;
import java.util.Stack;

import static com.razzzil.sphynx.commons.constant.Regex.VALID_VERSION_REGEX;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WorkerChangeVersionForm implements Validated {
    private Integer id;
    private String version;

    @Getter
    @AllArgsConstructor
    private enum  Fields {
        ID("id"),
        VERSION("version");
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
        if (Objects.isNull(version)) {
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.VERSION.getName())
                    .description(ValidationPresets.formatIsNull(Fields.VERSION.getName()))
                    .build());
        } else if (!VALID_VERSION_REGEX.matcher(version).find()) {
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.VERSION.getName())
                    .description(ValidationPresets.formatIsIncorrect(Fields.VERSION.getName()))
                    .build());
        }
        return validationResults;
    }
}

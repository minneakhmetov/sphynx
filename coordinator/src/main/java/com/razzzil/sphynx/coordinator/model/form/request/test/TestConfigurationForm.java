package com.razzzil.sphynx.coordinator.model.form.request.test;

import com.razzzil.sphynx.commons.constant.ValidationPresets;
import com.razzzil.sphynx.commons.model.testmode.TestMode;
import com.razzzil.sphynx.commons.model.webhook.WebhookModel;
import com.razzzil.sphynx.commons.validation.Validated;
import com.razzzil.sphynx.commons.validation.ValidationResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Objects;
import java.util.Stack;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class TestConfigurationForm implements Validated {
    private Integer databaseId;
    private String name;
    private String configs;
    private TestMode testMode;
    private String testModeConfigs;
    private String cron;
    private List<WebhookModel> webhookModels;

    @AllArgsConstructor
    @Getter
    public enum Fields {
        DATABASE_ID("databaseId"),
        NAME("name"),
        CONFIGS("configs"),
        TEST_MODE("testMode"),
        TEST_MODE_CONFIGS("testModeConfigs"),
        CRON("cron");
        private String name;
    }

    @Override
    public Stack<ValidationResult> validate() {
        Stack<ValidationResult> validationResults = new Stack<>();
        if (Objects.isNull(databaseId)) {
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.DATABASE_ID.getName())
                    .description(ValidationPresets.formatIsNull(Fields.DATABASE_ID.getName()))
                    .build());
        }
        if (Objects.isNull(name)){
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.NAME.getName())
                    .description(ValidationPresets.formatIsNull(Fields.NAME.getName()))
                    .build());
        }
        if (Objects.isNull(configs)){
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.CONFIGS.getName())
                    .description(ValidationPresets.formatIsNull(Fields.CONFIGS.getName()))
                    .build());
        }
        if (Objects.isNull(testMode)){
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.TEST_MODE.getName())
                    .description(ValidationPresets.formatIsNull(Fields.TEST_MODE.getName()))
                    .build());
        }
        if (Objects.isNull(testModeConfigs)){
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.TEST_MODE_CONFIGS.getName())
                    .description(ValidationPresets.formatIsNull(Fields.TEST_MODE_CONFIGS.getName()))
                    .build());
        }
//        if (Objects.isNull(cron)){
//            validationResults.add(ValidationResult.builder()
//                    .fieldName(Fields.CRON.getName())
//                    .description(ValidationPresets.formatIsNull(Fields.NAME.getName()))
//                    .build());
//        }
        return validationResults;
    }
}

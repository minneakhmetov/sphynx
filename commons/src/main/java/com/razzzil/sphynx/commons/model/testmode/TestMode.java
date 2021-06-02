package com.razzzil.sphynx.commons.model.testmode;

import com.fasterxml.jackson.core.type.TypeReference;
import com.razzzil.sphynx.commons.constant.ValidationPresets;
import com.razzzil.sphynx.commons.model.type.DatabaseType;
import com.razzzil.sphynx.commons.validation.Validated;
import com.razzzil.sphynx.commons.validation.ValidationResult;
import lombok.*;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Stack;

import static com.razzzil.sphynx.commons.constant.MaximumConstants.MAX_STEPS;
import static com.razzzil.sphynx.commons.constant.MaximumConstants.MAX_TIME_LIMIT;


@AllArgsConstructor
public enum TestMode {
    TIMED(TimedTestingModeConfig.class),
    STEPPED(SteppedTestingModeConfig.class);

    @EqualsAndHashCode(callSuper = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class SteppedTestingModeConfig extends AbstractTestingModeConfig {
        private Integer steps;

        @Getter
        @AllArgsConstructor
        public enum Fields {
            STEPS("steps");
            private String name;
        }

        @Override
        public TestMode testMode() {
            return TestMode.STEPPED;
        }

        @Override
        public Stack<ValidationResult> validate() {
            Stack<ValidationResult> validationResults = new Stack<>();
            if (Objects.isNull(steps)){
                validationResults.add(ValidationResult.builder()
                        .fieldName(Fields.STEPS.getName())
                        .description(ValidationPresets.formatIsNull(Fields.STEPS.getName()))
                        .build());
            } else if (steps < 0 || steps > MAX_STEPS){
                validationResults.add(ValidationResult.builder()
                        .fieldName("step")
                        .description(ValidationPresets.formatCustom("%s must be greater than 0 and less than %d", Fields.STEPS.getName(), MAX_STEPS))
                        .build());
            }
            return validationResults;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class TimedTestingModeConfig extends AbstractTestingModeConfig {
        private Integer timeLimit;
        private Boolean recursive;

        @Getter
        @AllArgsConstructor
        public enum Fields {
            TIME_LIMIT("timeLimit"),
            RECURSIVE("recursive");
            private String name;
        }

        @Override
        public TestMode testMode() {
            return TestMode.TIMED;
        }

        @Override
        public Stack<ValidationResult> validate() {
            Stack<ValidationResult> validationResults = new Stack<>();
            if (Objects.isNull(timeLimit)){
                validationResults.add(ValidationResult.builder()
                        .fieldName(Fields.TIME_LIMIT.getName())
                        .description(ValidationPresets.formatIsNull(Fields.TIME_LIMIT.getName()))
                        .build());
            } else if (timeLimit < 0 || timeLimit > MAX_TIME_LIMIT){
                validationResults.add(ValidationResult.builder()
                        .fieldName(Fields.TIME_LIMIT.getName())
                        .description(ValidationPresets.formatCustom("%s must be greater than 0 and less than %d", Fields.TIME_LIMIT.getName(), MAX_TIME_LIMIT))
                        .build());
            }
            if (Objects.isNull(recursive)) {
                validationResults.add(ValidationResult.builder()
                        .fieldName(Fields.RECURSIVE.getName())
                        .description(ValidationPresets.formatIsNull(Fields.RECURSIVE.getName()))
                        .build());
            }
            return validationResults;
        }
    }

    public static abstract class AbstractTestingModeConfig implements Validated {
        public abstract TestMode testMode();
    }

    @Getter
    private Class<? extends AbstractTestingModeConfig> referenceClass;

    public <T extends AbstractTestingModeConfig> TypeReference<T> getTypeReference() {
        return new TypeReference<>() {
            @Override
            public Type getType() {
                return referenceClass;
            }
        };
    }

    public static TestMode get(String value){
        for (TestMode testMode: values()){
            if (testMode.name().equals(value))
                return testMode;
        }
        throw new IllegalArgumentException("Test Mode " + value + " is not supported");
    }


}

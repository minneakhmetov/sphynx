package com.razzzil.sphynx.commons.model.payload;

import com.razzzil.sphynx.commons.constant.ValidationPresets;
import com.razzzil.sphynx.commons.validation.Validated;
import com.razzzil.sphynx.commons.validation.ValidationResult;
import lombok.*;

import java.util.Objects;
import java.util.Stack;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Payload implements Validated {
    private String endpoint;
    private Object body;
    private String sessionKey;

    @AllArgsConstructor
    @Getter
    public enum Fields {
        ENDPOINT("endpoint"),
        BODY("body"),
        SESSION_KEY("sessionKey");
        private String name;
    }

    @Override
    public Stack<ValidationResult> validate() {
        Stack<ValidationResult> validationResults = new Stack<>();
        if(Objects.isNull(endpoint)){
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.ENDPOINT.getName())
                    .description(ValidationPresets.formatIsNull(Fields.ENDPOINT.getName()))
                    .build());
        }
        if(Objects.isNull(body)){
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.BODY.getName())
                    .description(ValidationPresets.formatIsNull(Fields.BODY.getName()))
                    .build());
        }
        if(Objects.isNull(sessionKey)){
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.SESSION_KEY.getName())
                    .description(ValidationPresets.formatIsNull(Fields.SESSION_KEY.getName()))
                    .build());
        }
        return validationResults;
    }

    public static PayloadBuilder builder() {
        return new PayloadBuilder();
    }

    public static final class PayloadBuilder {
        private Payload payload;

        private PayloadBuilder() {
            payload = new Payload();
        }

        public PayloadBuilder endpoint(String endpoint) {
            payload.setEndpoint(endpoint);
            return this;
        }

        public PayloadBuilder body(Object body) {
            payload.setBody(body);
            return this;
        }

        public PayloadBuilder sessionKey(String sessionKey) {
            payload.setSessionKey(sessionKey);
            return this;
        }

        public Payload build() {
            return payload;
        }
    }
}

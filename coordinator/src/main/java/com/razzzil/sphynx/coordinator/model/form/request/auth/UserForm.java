package com.razzzil.sphynx.coordinator.model.form.request.auth;

import com.razzzil.sphynx.commons.constant.ValidationPresets;
import com.razzzil.sphynx.commons.validation.Validated;
import com.razzzil.sphynx.commons.validation.ValidationResult;
import com.razzzil.sphynx.commons.model.user.role.UserRole;
import lombok.*;
import org.passay.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.Stack;

import static com.razzzil.sphynx.commons.constant.Regex.VALID_EMAIL_ADDRESS_REGEX;
import static com.razzzil.sphynx.commons.constant.Regex.VALID_LOGIN_REGEX;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserForm implements Validated {
    private String email;
    private String login;
    private String password;
    private UserRole role;

    @Getter
    @AllArgsConstructor
    public enum Fields {
        EMAIL("email"),
        LOGIN("login"),
        PASSWORD("password"),
        ROLE("role");
        private String name;
    }

    @Override
    public Stack<ValidationResult> validate() {
        Stack<ValidationResult> validationResults = new Stack<>();
        if (Objects.isNull(email)) {
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.EMAIL.getName())
                    .description(ValidationPresets.formatIsNull(Fields.EMAIL.getName()))
                    .build());
        } else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.EMAIL.getName())
                    .description(ValidationPresets.formatIsIncorrect(Fields.EMAIL.getName()))
                    .build());
        }
        if (Objects.isNull(login)) {
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.LOGIN.getName())
                    .description(ValidationPresets.formatIsNull(Fields.LOGIN.getName()))
                    .build());
        } else if (!VALID_LOGIN_REGEX.matcher(login).find()) {
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.LOGIN.getName())
                    .description(ValidationPresets.formatIsIncorrect(Fields.LOGIN.getName()))
                    .build());
        }
        if (Objects.isNull(password)) {
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.PASSWORD.getName())
                    .description(ValidationPresets.formatIsNull(Fields.PASSWORD.getName()))
                    .build());
        } else {
            PasswordValidator validator = new PasswordValidator(Arrays.asList(
                    new LengthRule(8, 30),
                    new UppercaseCharacterRule(1),
                    new DigitCharacterRule(1),
                    new SpecialCharacterRule(1),
                    new NumericalSequenceRule(3, false),
                    new AlphabeticalSequenceRule(3, false),
                    new QwertySequenceRule(3, false),
                    new WhitespaceRule()));

            RuleResult result = validator.validate(new PasswordData(password));
            if (!result.isValid()) {
                validator.getMessages(result).forEach(message ->
                        validationResults.add(ValidationResult.builder()
                                .fieldName(Fields.PASSWORD.getName())
                                .description(message)
                                .build()));
            }
        }
        if (Objects.isNull(role)) {
            validationResults.add(ValidationResult.builder()
                    .fieldName(Fields.ROLE.getName())
                    .description(ValidationPresets.formatIsNull(Fields.ROLE.getName()))
                    .build());
        }
        return validationResults;
    }
}

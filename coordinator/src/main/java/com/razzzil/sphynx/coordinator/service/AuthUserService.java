package com.razzzil.sphynx.coordinator.service;

import com.razzzil.sphynx.commons.validation.ValidationResult;
import com.razzzil.sphynx.commons.util.Condition;
import com.razzzil.sphynx.commons.model.user.UserModel;
import com.razzzil.sphynx.coordinator.exception.EntityNotFoundException;
import com.razzzil.sphynx.coordinator.exception.IllegalFieldException;
import com.razzzil.sphynx.coordinator.exception.UnsuccessfulOperationException;
import com.razzzil.sphynx.coordinator.exception.WrongCredentialsException;
import com.razzzil.sphynx.coordinator.model.form.request.auth.LoginForm;
import com.razzzil.sphynx.coordinator.model.form.request.auth.UserForm;
import com.razzzil.sphynx.coordinator.model.form.request.auth.UserPrincipalClaims;
import com.razzzil.sphynx.coordinator.model.form.response.auth.AuthSuccessForm;
import com.razzzil.sphynx.coordinator.model.form.response.exception.EntityNotFoundExceptionResponse;
import com.razzzil.sphynx.coordinator.model.form.response.exception.IllegalFieldExceptionResponse;
import com.razzzil.sphynx.coordinator.model.form.response.exception.UnsuccessfulOperationExceptionResponse;
import com.razzzil.sphynx.coordinator.model.form.response.exception.WrongCredentialsExceptionResponse;
import com.razzzil.sphynx.coordinator.repository.UserRepository;;
import com.razzzil.sphynx.coordinator.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;

@Service
public class AuthUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public static final String ENTITY_NAME = "User";

    public AuthSuccessForm login(LoginForm loginForm) {
        WrongCredentialsException exception =
                new WrongCredentialsException(new WrongCredentialsExceptionResponse(ENTITY_NAME + " [" + loginForm.getLoginOrEmail() + "] is not correct"));

        UserModel userModel = userRepository.getByLoginOrEmail(loginForm.getLoginOrEmail())
                .orElseThrow(() -> exception);

        Condition.of(passwordEncoder.matches(loginForm.getPassword(), userModel.getHashedPassword()))
                .ifFalseThrow(() -> exception);

        String token = jwtUtil.generateToken(userModel);

        return AuthSuccessForm.builder()
                .claims(UserPrincipalClaims.fromUserModel(userModel))
                .token(token)
                .build();
    }

    public List<UserPrincipalClaims> getUsers(){
        return userRepository
                .getAll()
                .stream()
                .map(UserPrincipalClaims::fromUserModel)
                .collect(Collectors.toList());
    }

    public UserModel createUser(UserForm userForm) {
        Stack<ValidationResult> validationResults = userForm.validate();
        if (!validationResults.isEmpty()){
            throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(validationResults));
        }
        UserModel toDb = UserModel.builder()
                .role(userForm.getRole())
                .login(userForm.getLogin())
                .hashedPassword(passwordEncoder.encode(userForm.getPassword()))
                .email(userForm.getEmail())
                .build();

        UserModel fromdb = userRepository.save(toDb);
        return fromdb;
    }

    public UserModel updateUser(UserForm userForm, Integer id) {
        UserModel existing = getUserById(id);
        Stack<ValidationResult> validationResults = userForm.validate();
        if (Objects.isNull(userForm.getPassword()) || userForm.getPassword().isBlank()){
            validationResults.removeIf(validationResult ->
                    validationResult.getFieldName().equals(UserForm.Fields.PASSWORD.getName()));
        }
        if (!validationResults.isEmpty()){
            throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(validationResults));
        }
        UserModel model = UserModel.builder()
                .id(id)
                .role(Objects.nonNull(userForm.getRole()) ? userForm.getRole() : existing.getRole())
                .login(Objects.nonNull(userForm.getLogin()) && !userForm.getLogin().isBlank() ? userForm.getLogin() : existing.getLogin())
                .hashedPassword(Objects.nonNull(userForm.getPassword()) && !userForm.getPassword().isBlank() ? passwordEncoder.encode(userForm.getPassword()) : existing.getHashedPassword())
                .email(Objects.nonNull(userForm.getEmail()) && !userForm.getEmail().isBlank() ? userForm.getEmail() : existing.getEmail())
                .build();
        Condition.of(userRepository.update(model))
                .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(ENTITY_NAME,
                        String.format("There was an exception while updating user configs to database; UserId: %d", id))));
        return model;
    }

    public void deleteUser(Integer id) {
        Condition.of(userRepository.delete(id))
                .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(ENTITY_NAME,
                        String.format("There was an exception while writing to database; UserId %d", id))));
    }

    public UserModel getUserByLoginOrEmail(String loginOrEmail) {
        return userRepository
                .getByLoginOrEmail(loginOrEmail)
                .orElseThrow(() -> new EntityNotFoundException(new EntityNotFoundExceptionResponse(
                        ENTITY_NAME, String.format("User with login or email %s is not found", loginOrEmail))));
    }

    public UserModel getUserByLogin(String login) {
        return userRepository
                .getByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException(new EntityNotFoundExceptionResponse(
                        ENTITY_NAME, String.format("User with login %s is not found", login))));
    }

    public UserModel getUserByEmail(String email) {
        return userRepository
                .getByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(new EntityNotFoundExceptionResponse(
                        ENTITY_NAME, String.format("User with email %s is not found", email))));
    }

    public UserModel getUserById(Integer id) {
        return userRepository
                .getById(id)
                .orElseThrow(() -> new EntityNotFoundException(new EntityNotFoundExceptionResponse(
                        ENTITY_NAME, String.format("User with id %d is not found", id))));
    }

}

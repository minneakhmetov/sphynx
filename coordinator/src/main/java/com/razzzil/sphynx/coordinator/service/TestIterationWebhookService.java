package com.razzzil.sphynx.coordinator.service;

import com.razzzil.sphynx.commons.model.execution.TestExecutionModel;
import com.razzzil.sphynx.commons.model.savedquery.SavedQueryModel;
import com.razzzil.sphynx.commons.model.test.state.TestState;
import com.razzzil.sphynx.commons.model.webhook.WebhookModel;
import com.razzzil.sphynx.commons.model.worker.WorkerModel;
import com.razzzil.sphynx.commons.model.wrapper.IterationModelSaverQueryWrapper;
import com.razzzil.sphynx.commons.util.Condition;
import com.razzzil.sphynx.commons.model.database.DatabaseModel;
import com.razzzil.sphynx.commons.model.iteration.IterationModel;
import com.razzzil.sphynx.commons.model.iteration.configs.IterationConfigs;
import com.razzzil.sphynx.commons.model.test.TestModel;
import com.razzzil.sphynx.commons.model.test.configs.TestConfigs;
import com.razzzil.sphynx.commons.model.testmode.TestMode;
import com.razzzil.sphynx.commons.validation.ValidationResult;
import com.razzzil.sphynx.coordinator.exception.EntityNotFoundException;
import com.razzzil.sphynx.coordinator.exception.IllegalFieldException;
import com.razzzil.sphynx.coordinator.exception.UnsuccessfulOperationException;
import com.razzzil.sphynx.coordinator.jooq.tables.Webhook;
import com.razzzil.sphynx.coordinator.model.form.request.test.*;
import com.razzzil.sphynx.coordinator.model.form.response.exception.EntityNotFoundExceptionResponse;
import com.razzzil.sphynx.coordinator.model.form.response.exception.IllegalFieldExceptionResponse;
import com.razzzil.sphynx.coordinator.model.form.response.exception.UnsuccessfulOperationExceptionResponse;
import com.razzzil.sphynx.commons.model.wrapper.TestIterationModel;
import com.razzzil.sphynx.coordinator.repository.IterationRepository;
import com.razzzil.sphynx.coordinator.repository.TestRepository;
import com.razzzil.sphynx.coordinator.repository.WebhookRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TestIterationWebhookService {

    @Autowired
    private ExecutionService executionService;

    @Autowired
    private IterationRepository iterationRepository;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private TestSchedulerService testSchedulerService;

    @Autowired
    private WebhookRepository webhookRepository;

    @Autowired
    private SQLLabService sqlLabService;

    private static final String TEST_ENTITY_NAME = "Test";
    private static final String ITERATION_ENTITY_NAME = "Iteration";
    private static final String WEBHOOK_ENTITY_NAME = "Webhook";

    public List<TestModel> getAllTests(Integer userId) {
        return testRepository.getAllByUser(userId);
    }

    public List<IterationModel> getIterationsByTestAndUserId(Integer testId, Integer userId) {
        return iterationRepository.getAllByTestConfigsIdAndUserId(testId, userId);
    }

    public List<TestModel> getAllCronTests() {
        return testRepository.getAllCronTests();
    }

    public TestModel getTestAndByIdAndUserId(Integer testId, Integer userId) {
        TestModel model = testRepository
                .getTestByIdAndUserId(testId, userId)
                .orElseThrow(() -> new EntityNotFoundException(new EntityNotFoundExceptionResponse(
                        TEST_ENTITY_NAME, String.format("Test with id %d and user id %s is not found", testId, userId))));
        return model;
    }

//    public <T extends TestConfigs, V extends IterationConfigs, X extends TestMode.AbstractTestingModeConfig> TestIterationModel
//    saveTestIterations(TestIterationConfigurationForm testIterationConfigurationForm, Integer userId) {
//        DatabaseModel databaseModel = databaseService.getDatabaseByIdAndUser(userId, testIterationConfigurationForm.getTest().getDatabaseId());
//        T testConfigs = TestModel.deserializeConfigs(testIterationConfigurationForm.getTest().getConfigs(), databaseModel.getType());
//
//        Stack<ValidationResult> testValidationResults = testConfigs.validate();
//        if (!testValidationResults.empty()) {
//            throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(testValidationResults));
//        }
//
//        X testModeConfigs = TestModel.deserializeTestModeConfigs(testIterationConfigurationForm.getTest().getTestModeConfigs(),
//                testIterationConfigurationForm.getTest().getTestMode());
//        Stack<ValidationResult> testModeValidationResults = testModeConfigs.validate();
//        if (!testModeValidationResults.empty()) {
//            throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(testModeValidationResults));
//        }
//
//        List<V> iterationConfigs = testIterationConfigurationForm
//                .getIterations()
//                .stream()
//                .<V>map(model -> IterationModel.deserialize(model.getConfigs(), testConfigs.databaseType()))
//                .collect(Collectors.toList());
//
//        iterationConfigs.forEach(config -> {
//            Stack<ValidationResult> iterationValidationResults = config.validate();
//            if (!iterationValidationResults.empty()) {
//                throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(iterationValidationResults));
//            }
//        });
//
//        TestModel testModelToDb = TestModel.builder()
//                .configs(testConfigs)
//                .testModeConfigs(testModeConfigs)
//                .databaseId(testIterationConfigurationForm.getTest().getDatabaseId())
//                .workerId(databaseModel.getWorkerId())
//                .name(testIterationConfigurationForm.getTest().getName())
//                .userId(userId)
//                .build();
//
//        CronTrigger cronTrigger = null;
//        if (Objects.nonNull(testIterationConfigurationForm.getTest().getCron()) && !testIterationConfigurationForm.getTest().getCron().isBlank()) {
//            try {
//                cronTrigger = new CronTrigger(testIterationConfigurationForm.getTest().getCron());
//                testModelToDb.setCron(testIterationConfigurationForm.getTest().getCron());
//                testModelToDb.setNextExecutionTime(testSchedulerService.getNextExecutionTime(cronTrigger));
//            } catch (IllegalArgumentException e) {
//                ValidationResult vr = ValidationResult.builder()
//                        .fieldName(TestConfigurationForm.Fields.CRON.getName())
//                        .description(e.getMessage())
//                        .build();
//                throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(vr));
//            }
//        }
//
//        TestModel testModelFromDb = testRepository.save(testModelToDb);
//
//        List<IterationModel> iterationModelsDb = iterationConfigs.stream().map(configs ->
//                IterationModel.builder()
//                        .configs(configs)
//                        .databaseId(databaseModel.getId())
//                        .userId(userId)
//                        .testConfigId(testModelFromDb.getId())
//                        .build())
//                .collect(Collectors.toList());
//
//        List<IterationModel> iterationModelsFromDb = iterationRepository.save(iterationModelsDb);
//
//        List<Pair<IterationModel, SavedQueryModel>> pairs = new ArrayList<>();
//        iterationModelsFromDb.forEach(iterationModel -> {
//            if (!iterationModel.getSavedQueryId().equals(0)) {
//                SavedQueryModel savedQueryModel = sqlLabService.getSavedQueryByIdAndUserId(iterationModel.getSavedQueryId(), userId);
//                pairs.add(Pair.of(iterationModel, savedQueryModel));
//            } else {
//                pairs.add(Pair.of(iterationModel, null));
//            }
//        });
//
//        TestExecutionModel testExecutionModel = executionService.setCreated(testModelFromDb.getId(), userId);
//
//        if (Objects.nonNull(cronTrigger)) {
//            testSchedulerService.scheduleTest(testModelFromDb.getId(), testModelFromDb.getUserId(), cronTrigger);
//        }
//        List<WebhookModel> webhookModels = new ArrayList<>();
//        if (Objects.nonNull(testIterationConfigurationForm.getTest().getWebhookModels()) && !testIterationConfigurationForm.getTest().getWebhookModels().isEmpty()) {
//            testIterationConfigurationForm.getTest().getWebhookModels().forEach(webhookModel -> {
//                webhookModel.setUserId(userId);
//                webhookModel.setTestId(testModelFromDb.getId());
//                webhookModels.add(webhookRepository.save(webhookModel));
//            });
//        }
//        testModelFromDb.setWebhookModels(webhookModels);
//        testModelFromDb.setProcess(0);
//        testModelFromDb.setState(TestState.CREATED);
//        testModelFromDb.setLastExecutionId(testExecutionModel.getId());
//
//        return TestIterationModel.builder()
//                .test(testModelFromDb)
//                .iterations(pairs)
//                .build();
//    }

    public TestIterationModel getTestWithIterations(Integer id, Integer userId) {
        TestModel testModel = testRepository
                .getTestByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException(new EntityNotFoundExceptionResponse(
                        TEST_ENTITY_NAME, String.format("Test with id %d and user id %s is not found", id, userId))));

        List<IterationModel> iterationModels = iterationRepository.getAllByTestConfigsId(testModel.getId());

        List<IterationModelSaverQueryWrapper> pairs = new ArrayList<>();
        iterationModels.forEach(iterationModel -> {
            if (!iterationModel.getSavedQueryId().equals(0)) {
                SavedQueryModel savedQueryModel = sqlLabService.getSavedQueryByIdAndUserId(iterationModel.getSavedQueryId(), userId);
                pairs.add(IterationModelSaverQueryWrapper.builder()
                        .iterationModel(iterationModel)
                        .savedQueryModel(savedQueryModel)
                        .build());
            } else {
                pairs.add(IterationModelSaverQueryWrapper.builder()
                        .iterationModel(iterationModel)
                        .savedQueryModel(null)
                        .build());
            }
        });

        return TestIterationModel.builder()
                .iterations(pairs)
                .test(testModel)
                .build();
    }

//    public <T extends TestConfigs, V extends IterationConfigs, X extends TestMode.AbstractTestingModeConfig> TestIterationModel
//    updateTestIterations(TestIterationConfigurationUpdateForm testIterationConfigurationForm, Integer userId) {
//        DatabaseModel databaseModel = databaseService.getDatabaseByIdAndUser(userId, testIterationConfigurationForm.getTest().getDatabaseId());
//        T testConfigs = TestModel.deserializeConfigs(testIterationConfigurationForm.getTest().getConfigs(), databaseModel.getType());
//
//        Stack<ValidationResult> testValidationResults = testConfigs.validate();
//        if (!testValidationResults.empty()) {
//            throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(testValidationResults));
//        }
//
//        X testModeConfigs = TestModel.deserializeTestModeConfigs(testIterationConfigurationForm.getTest().getTestModeConfigs(),
//                testIterationConfigurationForm.getTest().getTestMode());
//        Stack<ValidationResult> testModeValidationResults = testModeConfigs.validate();
//        if (!testModeValidationResults.empty()) {
//            throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(testModeValidationResults));
//        }
//
//        Map<Integer, V> iterationConfigs = testIterationConfigurationForm
//                .getIterations()
//                .stream()
//                .collect(Collectors.toMap(IterationConfigUpdateForm::getId,
//                        model -> IterationModel.deserialize(model.getConfigs(), testConfigs.databaseType()), (a, b) -> b));
//
//        iterationConfigs.forEach((key, value) -> {
//            Stack<ValidationResult> iterationValidationResults = value.validate();
//            if (!iterationValidationResults.empty()) {
//                List<IllegalFieldExceptionResponse> responses = IllegalFieldExceptionResponse.fromValidationResult(iterationValidationResults);
//                responses.forEach(response -> response.setDescription(response.getDescription() + "; IterationId: " + key));
//                throw new IllegalFieldException(responses);
//            }
//        });
//
//        TestModel existingTestModel = testRepository.getTestByIdAndUserId(testIterationConfigurationForm.getTest().getId(), userId)
//                .orElseThrow(() -> new EntityNotFoundException(new EntityNotFoundExceptionResponse(
//                        TEST_ENTITY_NAME, String.format("Test with id %d and user id %s is not found", testIterationConfigurationForm.getTest().getId(), userId))));
//
//        TestModel testModelToDb = TestModel.builder()
//                .id(existingTestModel.getId())
//                .configs(testConfigs)
//                .testModeConfigs(testModeConfigs)
//                .databaseId(testIterationConfigurationForm.getTest().getDatabaseId())
//                .name(testIterationConfigurationForm.getTest().getName())
//                .userId(userId)
//                .build();
//
//        CronTrigger cronTrigger = null;
//        if (Objects.nonNull(testIterationConfigurationForm.getTest().getCron()) && !testIterationConfigurationForm.getTest().getCron().isBlank()) {
//            try {
//                cronTrigger = new CronTrigger(testIterationConfigurationForm.getTest().getCron());
//                testModelToDb.setCron(testIterationConfigurationForm.getTest().getCron());
//                testModelToDb.setNextExecutionTime(testSchedulerService.getNextExecutionTime(cronTrigger));
//            } catch (IllegalArgumentException e) {
//                ValidationResult vr = ValidationResult.builder()
//                        .fieldName(TestConfigurationForm.Fields.CRON.getName())
//                        .description(e.getMessage())
//                        .build();
//                throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(vr));
//            }
//        }
//        if (Objects.nonNull(existingTestModel.getCron()) && !existingTestModel.getCron().isBlank() && Objects.nonNull(existingTestModel.getNextExecutionTime())) {
//            testSchedulerService.removeScheduledTest(testModelToDb.getId(), testModelToDb.getUserId());
//        }
//
//        Condition.of(testRepository.update(testModelToDb))
//                .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(TEST_ENTITY_NAME,
//                        String.format("There was an exception while updating test configs to database; TestConfigId: %d, UserId: %d", testModelToDb.getId(), userId))));
//
//        List<IterationModel> iterationModelsDb = iterationConfigs
//                .entrySet()
//                .stream()
//                .map(entry ->
//                        IterationModel.builder()
//                                .configs(entry.getValue())
//                                .databaseId(databaseModel.getId())
//                                .userId(userId)
//                                .testConfigId(testModelToDb.getId())
//                                .id(entry.getKey())
//                                .build())
//                .collect(Collectors.toList());
//
//        List<Pair<IterationModel, SavedQueryModel>> pairs = new ArrayList<>();
//        iterationModelsDb.forEach(iterationModel -> {
//            if (!iterationModel.getSavedQueryId().equals(0)) {
//                SavedQueryModel savedQueryModel = sqlLabService.getSavedQueryByIdAndUserId(iterationModel.getSavedQueryId(), userId);
//                pairs.add(Pair.of(iterationModel, savedQueryModel));
//            } else {
//                pairs.add(Pair.of(iterationModel, null));
//            }
//        });
//
//        Condition.of(iterationRepository.update(iterationModelsDb))
//                .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(TEST_ENTITY_NAME,
//                        String.format("There was an exception while updating iteration configs to database; TestConfigId: %d; UserId: %d", testModelToDb.getId(), userId))));
//
//        if (Objects.nonNull(cronTrigger)) {
//            testSchedulerService.scheduleTest(testModelToDb.getId(), testModelToDb.getUserId(), cronTrigger);
//        }
//
//        if (Objects.nonNull(testIterationConfigurationForm.getTest().getWebhookModels())) {
//            List<WebhookModel> existing = webhookRepository.getAllByUserIdAndTestId(testModelToDb.getId(), testModelToDb.getUserId());
//            if (!testIterationConfigurationForm.getTest().getWebhookModels().isEmpty()) {
//                if (!existing.isEmpty()) {
//                    existing.removeAll(testIterationConfigurationForm.getTest().getWebhookModels());
//                    for (WebhookModel webhookModel : existing) {
//                        boolean found = false;
//                        WebhookModel ifChanged = null;
//                        for (WebhookModel candidate : testIterationConfigurationForm.getTest().getWebhookModels()) {
//                            if (Objects.nonNull(candidate.getId())) {
//                                if (candidate.getId().equals(webhookModel.getId())) {
//                                    found = true;
//                                }
//                                if (!candidate.equals(webhookModel)) {
//                                    ifChanged = candidate;
//                                    break;
//                                }
//                            } else {
//                                candidate.setTestId(testModelToDb.getId());
//                                candidate.setUserId(testModelToDb.getUserId());
//                                candidate.setId(webhookRepository.save(candidate).getId());
//                            }
//                        }
//                        if (!found) {
//                            webhookRepository.deleteByIdAndUserId(webhookModel.getId(), webhookModel.getUserId());
//                        }
//                        if (Objects.nonNull(ifChanged)) {
//                            Condition.of(webhookRepository.update(ifChanged))
//                                    .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(WEBHOOK_ENTITY_NAME,
//                                            String.format("There was an exception while updating webhook to database; TestId: %d, UserId: %d, Id: %d",
//                                                    testModelToDb.getId(), webhookModel.getUserId(), webhookModel.getId()))));
//                        }
//                    }
//                    testModelToDb.setWebhookModels(testIterationConfigurationForm.getTest().getWebhookModels());
//                } else {
//                    testIterationConfigurationForm.getTest().getWebhookModels().forEach(model -> {
//                        model.setTestId(testModelToDb.getId());
//                        model.setUserId(testModelToDb.getUserId());
//                        model.setId(webhookRepository.save(model).getId());
//                    });
//                }
//            } else if (!existing.isEmpty()) {
//                existing.forEach(model -> webhookRepository.deleteByIdAndUserId(model.getId(), model.getUserId()));
//            }
//        }
//
//        return TestIterationModel.builder()
//                .test(testModelToDb)
//                .iterations(iterationModelsDb)
//                .build();
//
//    }

    public <T extends TestConfigs, X extends TestMode.AbstractTestingModeConfig> TestModel createTest(TestConfigurationForm testModelForm, Integer userId) {
        Stack<ValidationResult> iterationConfigFormValRes = testModelForm.validate();
        if (!iterationConfigFormValRes.empty()) {
            throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(iterationConfigFormValRes));
        }

        DatabaseModel databaseModel = databaseService.getDatabaseByIdAndUser(userId, testModelForm.getDatabaseId());
        T testConfigs = TestModel.deserializeConfigs(testModelForm.getConfigs(), databaseModel.getType());

        Stack<ValidationResult> testValidationResults = testConfigs.validate();
        if (!testValidationResults.empty()) {
            throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(testValidationResults));
        }

        X testModeConfigs = TestModel.deserializeTestModeConfigs(testModelForm.getTestModeConfigs(),
                testModelForm.getTestMode());
        Stack<ValidationResult> testModeValidationResults = testModeConfigs.validate();
        if (!testModeValidationResults.empty()) {
            throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(testModeValidationResults));
        }
        TestModel testModelToDb = TestModel.builder()
                .configs(testConfigs)
                .testModeConfigs(testModeConfigs)
                .databaseId(testModelForm.getDatabaseId())
                .workerId(databaseModel.getWorkerId())
                .name(testModelForm.getName())
                .userId(userId)
                .build();

        CronTrigger cronTrigger = null;
        if (Objects.nonNull(testModelForm.getCron()) && !testModelForm.getCron().isBlank()) {
            try {
                cronTrigger = new CronTrigger(testModelForm.getCron());
                testModelToDb.setCron(testModelForm.getCron());
                testModelToDb.setNextExecutionTime(testSchedulerService.getNextExecutionTime(cronTrigger));
            } catch (IllegalArgumentException e) {
                ValidationResult vr = ValidationResult.builder()
                        .fieldName(TestConfigurationForm.Fields.CRON.getName())
                        .description(e.getMessage())
                        .build();
                throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(vr));
            }
        }

        TestModel testModelFromDb = testRepository.save(testModelToDb);
        TestExecutionModel testExecutionModel = executionService.setCreated(testModelFromDb.getId(), userId);

        if (Objects.nonNull(cronTrigger)) {
            testSchedulerService.scheduleTest(testModelFromDb.getId(), testModelFromDb.getUserId(), cronTrigger);
        }
        List<WebhookModel> webhookModels = new ArrayList<>();
        if (Objects.nonNull(testModelForm.getWebhookModels()) && !testModelForm.getWebhookModels().isEmpty()) {
            testModelForm.getWebhookModels().forEach(webhookModel -> {
                webhookModel.setUserId(userId);
                webhookModel.setTestId(testModelFromDb.getId());
                webhookModels.add(webhookRepository.save(webhookModel));
            });
        }
        testModelFromDb.setWebhookModels(webhookModels);
        testModelFromDb.setProcess(0);
        testModelFromDb.setState(TestState.CREATED);
        testModelFromDb.setLastExecutionId(testExecutionModel.getId());
        return testModelFromDb;
    }

    public <V extends IterationConfigs> IterationModel createIteration(IterationConfigForm iterationConfigForm, Integer userId, Integer testId) {
        Stack<ValidationResult> iterationConfigFormValRes = iterationConfigForm.validate();
        if (!iterationConfigFormValRes.empty()) {
            throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(iterationConfigFormValRes));
        }
        TestModel testModel = getTestAndByIdAndUserId(testId, userId);
        V iterationConfig = IterationModel.deserialize(iterationConfigForm.getConfigs(), testModel.getDatabaseType());
        Stack<ValidationResult> iterationValidationResult = iterationConfig.validate();
        if (!iterationValidationResult.empty()) {
            throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(iterationValidationResult));
        }
        IterationModel toDb = IterationModel.builder()
                .configs(iterationConfig)
                .databaseId(testModel.getDatabaseId())
                .userId(userId)
                .name(iterationConfigForm.getName())
                .sql(iterationConfigForm.getSql())
                .clean(iterationConfigForm.getClean())
                .savedQueryId(iterationConfigForm.getSavedQueryId())
                .testConfigId(testId)
                .build();
        return iterationRepository.save(toDb);
    }

    public <V extends IterationConfigs> IterationModel updateIteration(IterationConfigForm iterationConfigForm, Integer userId, Integer iterationId, Integer testId) {
        Stack<ValidationResult> iterationConfigFormValRes = iterationConfigForm.validate();
        if (!iterationConfigFormValRes.empty()) {
            throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(iterationConfigFormValRes));
        }

        IterationModel existing = iterationRepository.getByIterationIdUserIdAndTestId(iterationId, userId, testId)
                .orElseThrow(() -> new EntityNotFoundException(new EntityNotFoundExceptionResponse(
                        TEST_ENTITY_NAME, String.format("Iteration with id %d and user id %s and test id %s is not found", iterationId, userId, testId))));

        IterationModel toDb = IterationModel.builder()
                .databaseId(existing.getDatabaseId())
                .userId(userId)
                .name(Objects.nonNull(iterationConfigForm.getName()) && !iterationConfigForm.getName().isBlank() ? iterationConfigForm.getName() : existing.getName())
                .sql(Objects.nonNull(iterationConfigForm.getSql()) && !iterationConfigForm.getSql().isBlank() ? iterationConfigForm.getSql() : existing.getSql())
                .clean(Objects.nonNull(iterationConfigForm.getClean()) ? iterationConfigForm.getClean() : existing.getClean())
                .testConfigId(existing.getTestConfigsId())
                .savedQueryId(Objects.nonNull(iterationConfigForm.getSavedQueryId()) ? iterationConfigForm.getSavedQueryId() : existing.getSavedQueryId())
                .id(existing.getId())
                .build();

        TestModel testModel = getTestAndByIdAndUserId(existing.getTestConfigsId(), userId);

        if (Objects.nonNull(iterationConfigForm.getConfigs()) && !iterationConfigForm.getConfigs().isBlank()) {
            V iterationConfig = IterationModel.deserialize(iterationConfigForm.getConfigs(), testModel.getDatabaseType());
            Stack<ValidationResult> iterationValidationResult = iterationConfig.validate();
            if (!iterationValidationResult.empty()) {
                throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(iterationValidationResult));
            }
            toDb.setConfigsAsObject(iterationConfig);
        } else toDb.setConfigs(existing.getConfigs());

        Condition.of(iterationRepository.update(toDb))
                .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(TEST_ENTITY_NAME,
                        String.format("There was an exception while updating iteration configs to database; TestConfigId: %d; UserId: %d", iterationId, userId))));

        return toDb;
    }

    public <T extends TestConfigs, X extends TestMode.AbstractTestingModeConfig> TestModel updateTest(TestConfigurationForm testModelForm, Integer userId, Integer testId) {
        Stack<ValidationResult> testValRes = testModelForm.validate();
        if (!testValRes.empty()) {
            throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(testValRes));
        }

        TestModel existingTestModel = testRepository.getTestByIdAndUserId(testId, userId)
                .orElseThrow(() -> new EntityNotFoundException(new EntityNotFoundExceptionResponse(
                        TEST_ENTITY_NAME, String.format("Test with id %d and user id %s is not found", testId, userId))));

        TestModel testModelToDb = TestModel.builder()
                .userId(userId)
                .id(testId)
                .process(existingTestModel.getProcess())
                .testState(existingTestModel.getState())
                .lastExecutionId(existingTestModel.getLastExecutionId())
                .build();

        if (Objects.nonNull(testModelForm.getDatabaseId())) {
            DatabaseModel databaseModel = databaseService.getDatabaseByIdAndUser(userId, testModelForm.getDatabaseId());
            testModelToDb.setDatabaseId(databaseModel.getId());
            testModelToDb.setWorkerId(databaseModel.getWorkerId());
            testModelToDb.setDatabaseType(databaseModel.getType());
            List<IterationModel> iterationModels = iterationRepository.getAllByTestConfigsIdAndUserId(testId, userId);
            iterationModels.forEach(iterationModel -> {
                iterationModel.setDatabaseId(databaseModel.getId());
            });
            Condition.of(iterationRepository.update(iterationModels))
                    .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(ITERATION_ENTITY_NAME,
                            String.format("There was an exception while setting database ids to iterations; TestConfigId: %d; UserId: %d; DatabaseId: %d", testId, userId, databaseModel.getId()))));
        } else {
            testModelForm.setDatabaseId(existingTestModel.getDatabaseId());
            testModelToDb.setWorkerId(existingTestModel.getWorkerId());
            testModelToDb.setDatabaseType(existingTestModel.getDatabaseType());
        }

        if (Objects.nonNull(testModelForm.getName()) && !testModelForm.getName().isBlank()) {
            testModelToDb.setName(testModelForm.getName());
        } else testModelToDb.setName(existingTestModel.getName());

        if (Objects.nonNull(testModelForm.getConfigs()) && !testModelForm.getConfigs().isBlank()) {
            T testConfigs = TestModel.deserializeConfigs(testModelForm.getConfigs(), testModelToDb.getDatabaseType());
            Stack<ValidationResult> testValidationResults = testConfigs.validate();
            if (!testValidationResults.empty()) {
                throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(testValidationResults));
            }
            testModelToDb.setConfigsAsObject(testConfigs);
        } else testModelToDb.setConfigs(testModelForm.getConfigs());

        if (Objects.nonNull(testModelForm.getTestMode()) && Objects.nonNull(testModelForm.getTestModeConfigs()) && !testModelForm.getTestModeConfigs().isBlank()) {
            X testModeConfigs = TestModel.deserializeTestModeConfigs(testModelForm.getTestModeConfigs(),
                    testModelForm.getTestMode());
            Stack<ValidationResult> testModeValidationResults = testModeConfigs.validate();
            if (!testModeValidationResults.empty()) {
                throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(testModeValidationResults));
            }
            testModelToDb.setTestMode(testModelForm.getTestMode());
            testModelToDb.setTestModeConfigsByConfigs(testModeConfigs);
        } else {
            testModelToDb.setTestMode(existingTestModel.getTestMode());
            testModelToDb.setTestModeConfigs(existingTestModel.getTestModeConfigs());
        }

        CronTrigger cronTrigger = null;
        if (Objects.nonNull(testModelForm.getCron()) && !testModelForm.getCron().isBlank()) {
            try {
                cronTrigger = new CronTrigger(testModelForm.getCron());
                testModelToDb.setCron(testModelForm.getCron());
                testModelToDb.setNextExecutionTime(testSchedulerService.getNextExecutionTime(cronTrigger));
            } catch (IllegalArgumentException e) {
                ValidationResult vr = ValidationResult.builder()
                        .fieldName(TestConfigurationForm.Fields.CRON.getName())
                        .description(e.getMessage())
                        .build();
                throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(vr));
            }
        }
        if (Objects.nonNull(existingTestModel.getCron()) && !existingTestModel.getCron().isBlank() && Objects.nonNull(existingTestModel.getNextExecutionTime())) {
            testSchedulerService.removeScheduledTest(testModelToDb.getId(), testModelToDb.getUserId());
        }

        Condition.of(testRepository.update(testModelToDb))
                .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(ITERATION_ENTITY_NAME,
                        String.format("There was an exception while writing to database; TestConfigId: %d; UserId: %d", testId, userId))));

        if (Objects.nonNull(cronTrigger)) {
            testSchedulerService.scheduleTest(testModelToDb.getId(), testModelToDb.getUserId(), cronTrigger);
        }

        if (Objects.nonNull(testModelForm.getWebhookModels())) {
            List<WebhookModel> existing = webhookRepository.getAllByUserIdAndTestId(testModelToDb.getId(), testModelToDb.getUserId());
            if (!testModelForm.getWebhookModels().isEmpty()) {
                if (!existing.isEmpty()) {
                    existing.removeAll(testModelForm.getWebhookModels());
                    for (WebhookModel webhookModel : existing) {
                        boolean found = false;
                        WebhookModel ifChanged = null;
                        for (WebhookModel candidate : testModelForm.getWebhookModels()) {
                            if (Objects.nonNull(candidate.getId())) {
                                if (candidate.getId().equals(webhookModel.getId())) {
                                    found = true;
                                }
                                if (!candidate.equals(webhookModel)) {
                                    ifChanged = candidate;
                                    break;
                                }
                            } else {
                                candidate.setTestId(testModelToDb.getId());
                                candidate.setUserId(testModelToDb.getUserId());
                                candidate.setId(webhookRepository.save(candidate).getId());
                            }
                        }
                        if (!found) {
                            webhookRepository.deleteByIdAndUserId(webhookModel.getId(), webhookModel.getUserId());
                        }
                        if (Objects.nonNull(ifChanged)) {
                            Condition.of(webhookRepository.update(ifChanged))
                                    .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(WEBHOOK_ENTITY_NAME,
                                            String.format("There was an exception while updating webhook to database; TestId: %d, UserId: %d, Id: %d",
                                                    testModelToDb.getId(), webhookModel.getUserId(), webhookModel.getId()))));

                        }
                    }
                    testModelToDb.setWebhookModels(testModelForm.getWebhookModels());
                } else {
                    testModelForm.getWebhookModels().forEach(model -> {
                        model.setTestId(testModelToDb.getId());
                        model.setUserId(testModelToDb.getUserId());
                        model.setId(webhookRepository.save(model).getId());
                    });
                }
            } else if (!existing.isEmpty()) {
                existing.forEach(model -> webhookRepository.deleteByIdAndUserId(model.getId(), model.getUserId()));
            }
        }

        return testModelToDb;
    }


    public void deleteIterationById(Integer testConfigsId, Integer iterationId, Integer userId) {
        Condition.of(iterationRepository.deleteByTestConfigsIdAndUserId(iterationId, testConfigsId, userId))
                .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(ITERATION_ENTITY_NAME,
                        String.format("There was an exception while writing to database; TestConfigId: %d, IterationId: %d, UserId: %d", testConfigsId, iterationId, userId))));
    }

    public void deleteTestById(Integer testConfigsId, Integer userId) {
        iterationRepository.deleteByTestIdAndUserId(testConfigsId, userId);
        executionService.deleteByTestWithMetrics(testConfigsId, userId);
        webhookRepository.deleteByTestIdAndUserId(testConfigsId, userId);
//        Condition.of(iterationRepository.deleteByTestIdAndUserId(testConfigsId, userId))
//                .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(ITERATION_ENTITY_NAME,
//                        String.format("There was an exception while writing to database; TestConfigId: %d, UserId: %d", testConfigsId, userId))));
        Condition.of(testRepository.deleteByUser(testConfigsId, userId))
                .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(ITERATION_ENTITY_NAME,
                        String.format("There was an exception while writing to database; TestConfigId: %d, UserId: %d", testConfigsId, userId))));
    }

    public void deleteByDatabaseId(Integer databaseId, Integer userId) {
        iterationRepository.deleteByDatabaseIdAndUserId(databaseId, userId);
        testRepository.getAllByDatabaseIdAndUserId(databaseId, userId).forEach(testModel -> {
            executionService.deleteByTestWithMetrics(testModel.getId(), userId);
            webhookRepository.deleteByTestIdAndUserId(testModel.getId(), userId);
        });
//        Condition.of(iterationRepository.deleteByDatabaseIdAndUserId(databaseId, userId))
//                .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(ITERATION_ENTITY_NAME,
//                        String.format("There was an exception while writing to database [DELETE BY DATABASE]; DatabaseId: %d, UserId: %d", databaseId, userId))));
        Condition.of(testRepository.deleteByDatabaseIdAndUserId(databaseId, userId))
                .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(ITERATION_ENTITY_NAME,
                        String.format("There was an exception while writing to database [DELETE BY DATABASE]; DatabaseId: %d, UserId: %d", databaseId, userId))));
    }

    public void updateNextExecutionTime(Integer testId, LocalDateTime nextExecutionTime) {
        Condition.of(testRepository.updateNextExecutionTime(testId, nextExecutionTime))
                .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(ITERATION_ENTITY_NAME,
                        String.format("There was an exception while updating execution time to database; TestConfigId: %d", testId))));
    }


}

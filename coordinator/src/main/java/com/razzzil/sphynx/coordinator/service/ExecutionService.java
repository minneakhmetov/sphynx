package com.razzzil.sphynx.coordinator.service;

import com.razzzil.sphynx.commons.model.database.DatabaseModel;
import com.razzzil.sphynx.commons.model.execution.TestExecutionModel;
import com.razzzil.sphynx.commons.model.metrics.MetricsModel;
import com.razzzil.sphynx.commons.model.payload.Payload;
import com.razzzil.sphynx.commons.model.savedquery.SavedQueryModel;
import com.razzzil.sphynx.commons.model.test.TestModel;
import com.razzzil.sphynx.commons.model.test.state.TestState;
import com.razzzil.sphynx.commons.model.wrapper.TestIterationModel;
import com.razzzil.sphynx.commons.model.wrapper.TestStartWrapper;
import com.razzzil.sphynx.commons.model.wrapper.TestUpdateStateWrapper;
import com.razzzil.sphynx.commons.util.Condition;
import com.razzzil.sphynx.coordinator.exception.*;
import com.razzzil.sphynx.coordinator.model.form.request.worker.WorkerDisplayForm;
import com.razzzil.sphynx.coordinator.model.form.response.exception.*;
import com.razzzil.sphynx.coordinator.repository.MetricsRepository;
import com.razzzil.sphynx.coordinator.repository.TestExecutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExecutionService {

    private static final String ENTITY_NAME = "Execution";

    @Autowired
    private TestIterationWebhookService testIterationWebhookService;

    @Autowired
    private MetricsRepository metricsRepository;

    @Autowired
    private TestExecutionRepository testExecutionRepository;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private SQLLabService sqlLabService;

    public List<TestExecutionModel> getAllExecutionsByTestIdAndUserId(Integer testId, Integer userId) {
        return testExecutionRepository
                .getTestExecutionModelsByUserIdAndTestConfigsId(testId, userId)
                .stream()
                .filter(testExecutionModel -> !testExecutionModel.getState().isFiltered())
                .collect(Collectors.toList());
    }

    public List<MetricsModel> getMetricsByExecutionIdAndUserId(Integer executionId, Integer userId) {
        return metricsRepository.getByTestExecutionIdAndUserId(executionId, userId);
    }

    public TestExecutionModel startTest(Integer testId, Integer userId) {
        List<TestExecutionModel> testExecutionModels = testExecutionRepository.getTestExecutionModelsByUserIdAndTestConfigsId(testId, userId);
        testExecutionModels
                .stream()
                .filter(model -> TestState.ORDERED.equals(model.getState()) || TestState.PAUSED.equals(model.getState()) || TestState.RUNNING.equals(model.getState()) || TestState.PENDING.equals(model.getState()))
                .forEachOrdered(model -> {
                    throw new TestHasBeenAlreadyProcessingException(new TestHasBeenAlreadyProcessingExceptionResponse(testId));
                });
        TestIterationModel ticsf = testIterationWebhookService.getTestWithIterations(testId, userId);

        if (ticsf.getIterations().isEmpty()){
            throw new IterationsEmptyException(new IterationsEmptyExceptionResponse(String.format("Test %s does not have iterations", ticsf.getTest().getName())));
        }

        WorkerDisplayForm displayForm = workerService.getDisplayWorkerById(ticsf.getTest().getWorkerId());

        if (!workerService.isAlive(ticsf.getTest().getWorkerId())){
            throw new WorkerNotAvailableException(new WorkerNotAvailableExceptionResponse(ticsf.getTest().getWorkerId(), displayForm.getAlias()));
        }

        TestExecutionModel toDb = TestExecutionModel.builder()
                .testConfigsId(testId)
                .startTime(LocalDateTime.now())
                .state(TestState.PENDING)
                .userId(userId)
                .build();
        TestExecutionModel fromDb = testExecutionRepository.save(toDb);

        DatabaseModel databaseModel = databaseService.getDatabaseByIdAndUser(userId, ticsf.getTest().getDatabaseId());

        TestStartWrapper testStartWrapper = TestStartWrapper.builder()
                .testExecutionModel(fromDb)
                .testIterationModel(ticsf)
                .databaseModel(databaseModel)
                .build();
        Payload payload = Payload.builder()
                .endpoint("startTest")
                .body(testStartWrapper)
                .build();
        workerService.sendMessage(databaseModel.getWorkerId(), payload);
        fromDb.setProcess(0);
        return fromDb;
    }

    public TestUpdateStateWrapper terminate(Integer executionId, Integer userId) {
        return updateState(executionId, userId, null, TestState.PENDING, "terminateTest", false);
    }

    public TestUpdateStateWrapper pause(Integer executionId, Integer userId) {
        return updateState(executionId, userId, null, TestState.PENDING, "pauseTest", false);
    }

    public TestUpdateStateWrapper resume(Integer executionId, Integer userId) {
        return updateState(executionId, userId, null, TestState.PENDING, "resumeTest", false);
    }

    private TestUpdateStateWrapper updateState(Integer executionId, Integer userId, String message, TestState testState, String endpoint, boolean setEndDate) {
        TestExecutionModel testExecutionModel = updateStateDb(executionId, userId, message, testState, setEndDate);
        TestModel ticsf = testIterationWebhookService.getTestAndByIdAndUserId(testExecutionModel.getTestConfigsId(), userId);
        TestUpdateStateWrapper testUpdateStateWrapper = new TestUpdateStateWrapper(executionId, ticsf.getWorkerId());
        Payload payload = Payload.builder()
                .endpoint(endpoint)
                .body(testUpdateStateWrapper)
                .build();
        workerService.sendMessage(ticsf.getWorkerId(), payload);
        return testUpdateStateWrapper;
    }

    public TestExecutionModel updateStateDb(Integer executionId, Integer userId, String message, TestState testState, boolean setEndDate) {
        TestExecutionModel testExecutionModel = getTestExecutionModel(executionId, userId);
        testExecutionModel.setState(testState);
        testExecutionModel.setMessage(message);
        if (setEndDate) {
            testExecutionModel.setEndTime(LocalDateTime.now());
        }
        Condition.of(testExecutionRepository.update(testExecutionModel)).ifFalseThrow(
                () -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(ENTITY_NAME,
                        String.format("There was an exception while updating execution configs to database; UserId: %d; ExecutionId: %d", userId, executionId))));
        return testExecutionModel;
    }

    public TestExecutionModel getTestExecutionModel(Integer executionId, Integer userId) {
        return testExecutionRepository.getTestExecutionModelByIdAndUserId(executionId, userId)
                .orElseThrow(() -> new EntityNotFoundException(new EntityNotFoundExceptionResponse(
                        ENTITY_NAME, String.format("Execution with id %d and user id %s is not found", executionId, userId))));
//        Integer process = metricsRepository.getMaxProcessByExecutionIdAndUserId(executionId, userId).orElse(null);
//        executionModel.setProcess(process);
    }

    @Async
    public void updateCleanState(Integer executionId, Integer userId, Boolean clean) {
        TestExecutionModel testExecutionModel = getTestExecutionModel(executionId, userId);
        if (testExecutionModel.getState().equals(TestState.RUNNING) && clean) {
            updateStateDb(executionId, userId, null, TestState.CLEANING, false);
        }
        if (testExecutionModel.getState().equals(TestState.CLEANING) && !clean) {
            updateStateDb(executionId, userId, null, TestState.RUNNING, false);
        }
    }

    public void deleteByTestWithMetrics(Integer testId, Integer userId){
        testExecutionRepository.deleteByTestIdAndUserId(testId, userId);
        metricsRepository.deleteByTestIdAndUserId(testId, userId);
    }

    public TestExecutionModel setCreated(Integer testId, Integer userId){
        TestExecutionModel toDb = TestExecutionModel.builder()
                .state(TestState.CREATED)
                .userId(userId)
                .startTime(LocalDateTime.now())
                .process(0)
                .testConfigsId(testId)
                .build();
        return testExecutionRepository.save(toDb);
    }




}

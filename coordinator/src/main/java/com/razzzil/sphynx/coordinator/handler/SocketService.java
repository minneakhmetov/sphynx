package com.razzzil.sphynx.coordinator.handler;

import com.razzzil.sphynx.commons.model.database.DatabaseDisplayForm;
import com.razzzil.sphynx.commons.model.database.DatabaseModel;
import com.razzzil.sphynx.commons.model.execution.TestExecutionModel;
import com.razzzil.sphynx.commons.model.metrics.MetricsModel;
import com.razzzil.sphynx.commons.model.savedquery.SavedQueryModel;
import com.razzzil.sphynx.commons.model.test.state.TestState;
import com.razzzil.sphynx.commons.model.worker.connectionstate.ConnectionState;
import com.razzzil.sphynx.commons.model.wrapper.MetricsModelWrapper;
import com.razzzil.sphynx.commons.model.wrapper.TestUserUpdateStateWrapper;
import com.razzzil.sphynx.commons.socket.annotation.SocketHandler;
import com.razzzil.sphynx.commons.socket.annotation.SocketMapping;
import com.razzzil.sphynx.coordinator.model.form.request.auth.UserPrincipalClaims;
import com.razzzil.sphynx.coordinator.model.form.request.worker.Worker;
import com.razzzil.sphynx.coordinator.model.form.response.metrics.ExecutionProcess;
import com.razzzil.sphynx.coordinator.model.form.websocket.UserPrincipalClaimsSubs;
import com.razzzil.sphynx.coordinator.repository.MetricsRepository;
import com.razzzil.sphynx.coordinator.service.DatabaseService;
import com.razzzil.sphynx.coordinator.service.ExecutionService;
import com.razzzil.sphynx.coordinator.service.SQLLabService;
import com.razzzil.sphynx.coordinator.webhook.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.annotation.Async;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SocketHandler
public class SocketService {

    @Autowired
    private MetricsRepository metricsRepository;

    @Autowired
    private ExecutionService executionService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private SimpUserRegistry simpUserRegistry;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private SQLLabService sqlLabService;

    @Autowired
    private WebhookService webhookService;

    @SocketMapping("logMetrics")
    public void logMetrics(MetricsModelWrapper metricsModelWrapper, Worker worker) {
        MetricsModel toDb = MetricsModel.fromWrapper(metricsModelWrapper);
        MetricsModel fromDb = metricsRepository.save(toDb);
        toDb.setId(fromDb.getId());
        executionService.updateCleanState(metricsModelWrapper.getTestExecutionId(), metricsModelWrapper.getUserId(), metricsModelWrapper.getClean());
        Map<Integer, UserPrincipalClaimsSubs> websocketUsers = getWebsocketUsersAndSubscriptions();
        UserPrincipalClaimsSubs userSubs = websocketUsers.get(fromDb.getUserId());
        String metricsEndpoint = String.format("/test/%d/execution/%d/metrics", fromDb.getTestId(), fromDb.getTestExecutionId());
        String processEndpoint = String.format("/test/%d/execution/%d/process", fromDb.getTestId(), fromDb.getTestExecutionId());
        if (Objects.nonNull(userSubs)) {
            if (userSubs.getSubscriptions().contains(metricsEndpoint)) {
                simpMessagingTemplate.convertAndSendToUser(userSubs.getUserPrincipalClaims().getName(), metricsEndpoint, toDb);
            }
            if (userSubs.getSubscriptions().contains(processEndpoint)) {
                simpMessagingTemplate.convertAndSendToUser(userSubs.getUserPrincipalClaims().getName(), processEndpoint, ExecutionProcess.fromMetricsModelWrapper(metricsModelWrapper));
            }
        }
    }

    @SocketMapping("finishTest")
    public void finish(TestUserUpdateStateWrapper testUserUpdateStateWrapper, Worker worker) {
        TestExecutionModel testExecutionModel = executionService.updateStateDb(testUserUpdateStateWrapper.getId(),
                testUserUpdateStateWrapper.getUserId(), testUserUpdateStateWrapper.getMessage(), TestState.FINISHED, true);
        this.sendExecutionModelToUsers(testExecutionModel);

    }

    @SocketMapping("runTest")
    public void run(TestUserUpdateStateWrapper testUserUpdateStateWrapper, Worker worker) {
        TestExecutionModel testExecutionModel = executionService.updateStateDb(testUserUpdateStateWrapper.getId(),
                testUserUpdateStateWrapper.getUserId(), testUserUpdateStateWrapper.getMessage(), TestState.RUNNING, false);
        this.sendExecutionModelToUsers(testExecutionModel);

    }

    @SocketMapping("failedTest")
    public void failed(TestUserUpdateStateWrapper testUserUpdateStateWrapper, Worker worker) {
        TestExecutionModel testExecutionModel = executionService.updateStateDb(testUserUpdateStateWrapper.getId(),
                testUserUpdateStateWrapper.getUserId(), testUserUpdateStateWrapper.getMessage(), TestState.FAILED, true);
        this.sendExecutionModelToUsers(testExecutionModel);
    }

    @SocketMapping("overdueTest")
    public void overdue(TestUserUpdateStateWrapper testUserUpdateStateWrapper, Worker worker) {
        TestExecutionModel testExecutionModel = executionService.updateStateDb(testUserUpdateStateWrapper.getId(),
                testUserUpdateStateWrapper.getUserId(), testUserUpdateStateWrapper.getMessage(), TestState.OVERDUE, true);
        this.sendExecutionModelToUsers(testExecutionModel);
    }

    @SocketMapping("pauseTest")
    public void pause(TestUserUpdateStateWrapper testUserUpdateStateWrapper, Worker worker) {
        TestExecutionModel testExecutionModel = executionService.updateStateDb(testUserUpdateStateWrapper.getId(),
                testUserUpdateStateWrapper.getUserId(), testUserUpdateStateWrapper.getMessage(), TestState.PAUSED, false);
        this.sendExecutionModelToUsers(testExecutionModel);
    }

    @SocketMapping("terminateTest")
    public void terminate(TestUserUpdateStateWrapper testUserUpdateStateWrapper, Worker worker) {
        TestExecutionModel testExecutionModel = executionService.updateStateDb(testUserUpdateStateWrapper.getId(),
                testUserUpdateStateWrapper.getUserId(), testUserUpdateStateWrapper.getMessage(), TestState.TERMINATED, true);
        this.sendExecutionModelToUsers(testExecutionModel);
    }

    @SocketMapping("orderedTest")
    public void ordered(TestUserUpdateStateWrapper testUserUpdateStateWrapper, Worker worker) {
        TestExecutionModel testExecutionModel = executionService.updateStateDb(testUserUpdateStateWrapper.getId(),
                testUserUpdateStateWrapper.getUserId(), testUserUpdateStateWrapper.getMessage(), TestState.ORDERED, false);
        this.sendExecutionModelToUsers(testExecutionModel);
    }

    @SocketMapping("databasePing")
    public void sendPingStatus(DatabaseDisplayForm databaseDisplayForm, Worker worker) {
        databaseService.updateConnectionState(databaseDisplayForm.getUserId(), databaseDisplayForm.getId(), databaseDisplayForm.getConnectionState());
        Map<Integer, UserPrincipalClaimsSubs> websocketUsers = getWebsocketUsersAndSubscriptions();
        UserPrincipalClaimsSubs userSubs = websocketUsers.get(databaseDisplayForm.getUserId());
        if (Objects.nonNull(userSubs)) {
            if (userSubs.getSubscriptions().contains("/databases")) {
                simpMessagingTemplate.convertAndSendToUser(userSubs.getUserPrincipalClaims().getName(), "/databases", databaseDisplayForm);
            }
        }
    }

    @SocketMapping("query")
    public void query(SavedQueryModel savedQueryModel, Worker worker) {
        sqlLabService.updateState(savedQueryModel);
        Map<Integer, UserPrincipalClaimsSubs> websocketUsers = getWebsocketUsersAndSubscriptions();
        UserPrincipalClaimsSubs userSubs = websocketUsers.get(savedQueryModel.getUserId());
        if (Objects.nonNull(userSubs)) {
            if (userSubs.getSubscriptions().contains("/sqllab")) {
                simpMessagingTemplate.convertAndSendToUser(userSubs.getUserPrincipalClaims().getName(), "/sqllab", savedQueryModel);
            }
        }
    }

    private void sendExecutionModelToUsers(TestExecutionModel testExecutionModel) {
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        exec.schedule(() -> {
            webhookService.process(testExecutionModel);
            String currentEndpoint = String.format("/test/%d/execution/%d", testExecutionModel.getTestConfigsId(), testExecutionModel.getId());
            Map<Integer, UserPrincipalClaimsSubs> websocketUsers = getWebsocketUsersAndSubscriptions();
            UserPrincipalClaimsSubs userSubs = websocketUsers.get(testExecutionModel.getUserId());
            if (Objects.nonNull(userSubs)) {
                if (userSubs.getSubscriptions().contains(currentEndpoint)) {
                    simpMessagingTemplate.convertAndSendToUser(userSubs.getUserPrincipalClaims().getName(), currentEndpoint, testExecutionModel);
                }
            }
        }, 500, TimeUnit.MILLISECONDS);
    }

    public Map<Integer, UserPrincipalClaimsSubs> getWebsocketUsersAndSubscriptions() {
        Map<Integer, UserPrincipalClaimsSubs> map = new HashMap<>();
        for (SimpUser simpUser : simpUserRegistry.getUsers()) {
            Set<String> subs = new HashSet<>();
            simpUser
                    .getSessions()
                    .forEach(simpSession -> subs
                            .addAll(simpSession
                                    .getSubscriptions()
                                    .stream()
                                    .map(simpSubscription -> simpSubscription.getDestination().replace("/user/" + simpUser.getPrincipal().getName(), ""))
                                    .collect(Collectors.toSet())));
            UserPrincipalClaimsSubs userPrincipalClaimsSubs = UserPrincipalClaimsSubs.builder()
                    .simpUser(simpUser)
                    .subscriptions(subs)
                    .userPrincipalClaims((UserPrincipalClaims) simpUser.getPrincipal())
                    .build();
            map.put(userPrincipalClaimsSubs.getUserPrincipalClaims().getId(), userPrincipalClaimsSubs);
        }
        return map;
    }


}

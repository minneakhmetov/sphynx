package com.razzzil.sphynx.coordinator.service;

import com.razzzil.sphynx.commons.model.test.TestModel;
import com.razzzil.sphynx.coordinator.model.TestScheduleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Component
@Scope("singleton")
public class TestSchedulerService {

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private ExecutionService executionService;

    @Autowired
    private TestIterationWebhookService testIterationWebhookService;

    private static final Map<Integer, TestScheduleModel> map = new HashMap<>();

    public void scheduleTest(Integer testId, Integer userId, CronTrigger cronTrigger){
        Runnable task = () -> {
            executionService.startTest(testId, userId);
            testIterationWebhookService.updateNextExecutionTime(testId, getNextExecutionTime(cronTrigger));
        };
        ScheduledFuture<?> future = taskScheduler.schedule(task, cronTrigger);
        TestScheduleModel testScheduleModel = TestScheduleModel.builder()
                .testId(testId)
                .task(task)
                .userId(userId)
                .future(future)
                .cronTrigger(cronTrigger)
                .build();
        map.put(testId, testScheduleModel);
    }

    public void scheduleTest(Integer testId, Integer userId, Date cronTriggerDate, Runnable task){
       // Runnable task = () -> executionService.startTest(testId, userId);
        ScheduledFuture<?> future = taskScheduler.schedule(task, cronTriggerDate);
        TestScheduleModel testScheduleModel = TestScheduleModel.builder()
                .testId(testId)
                .task(task)
                .userId(userId)
                .future(future)
                .date(cronTriggerDate)
                .build();
        map.put(testId, testScheduleModel);
    }

    public void removeScheduledTest(Integer testId, Integer userId) {
        TestScheduleModel model = map.get(testId);
        if (Objects.nonNull(model) && userId.equals(model.getUserId())){
            model.getFuture().cancel(false);
            map.remove(testId);
        }
    }

    public LocalDateTime getNextExecutionTime(CronTrigger cronTrigger){
        TriggerContext triggerContext = new SimpleTriggerContext(taskScheduler.getClock());
        Date date = cronTrigger.nextExecutionTime(triggerContext);
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup(){
        List<TestModel> tests = testIterationWebhookService.getAllCronTests();
        tests.forEach(testModel -> {
            Runnable task = () ->  {
                executionService.startTest(testModel.getId(), testModel.getUserId());
                Runnable cron = () -> executionService.startTest(testModel.getId(), testModel.getUserId());
                ScheduledFuture<?> future = taskScheduler.schedule(cron, new CronTrigger(testModel.getCron()));
                TestScheduleModel testScheduleModel = map.get(testModel.getId());
                testScheduleModel.setFuture(future);
                testScheduleModel.setTask(cron);
            };
            Date date = Date.from(testModel.getNextExecutionTime().atZone(ZoneId.systemDefault()).toInstant());
            scheduleTest(testModel.getId(), testModel.getUserId(), date, task);
        });
    }

}

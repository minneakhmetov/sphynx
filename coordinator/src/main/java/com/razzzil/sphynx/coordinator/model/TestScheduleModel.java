package com.razzzil.sphynx.coordinator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TestScheduleModel {
    private Integer testId;
    private Integer userId;
    private Runnable task;
    private ScheduledFuture<?> future;
    private CronTrigger cronTrigger;
    private Date date;
}

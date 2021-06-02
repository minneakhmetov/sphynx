package com.razzzil.sphynx.worker.model;

import com.razzzil.sphynx.commons.model.metrics.additional.AdditionalMetrics;
import com.razzzil.sphynx.commons.model.testmode.TestMode;
import com.razzzil.sphynx.commons.model.wrapper.MetricsModelWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StepResult {
    private Integer testExecutionId;
    private Integer testId;
    private LocalDateTime time;
    private Integer userId;
    private Integer stepId;
    private String message;
    private Boolean success;
    private Integer workerId;
    private List<Connection> connections;

    public static StepResult fromMetricsModelWrapper(MetricsModelWrapper metricsModelWrapper){
        return StepResult.builder()
                .testId(metricsModelWrapper.getTestId())
                .testExecutionId(metricsModelWrapper.getTestExecutionId())
                .message(metricsModelWrapper.getMessage())
                .stepId(metricsModelWrapper.getStepId())
                .success(metricsModelWrapper.getSuccess())
                .time(metricsModelWrapper.getTime())
                .userId(metricsModelWrapper.getUserId())
                .workerId(metricsModelWrapper.getWorkerId())
                .build();
    }
}

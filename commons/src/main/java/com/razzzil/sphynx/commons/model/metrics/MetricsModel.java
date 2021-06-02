package com.razzzil.sphynx.commons.model.metrics;

import com.razzzil.sphynx.commons.model.iteration.IterationModel;
import com.razzzil.sphynx.commons.model.metrics.additional.AdditionalMetrics;
import com.razzzil.sphynx.commons.model.testmode.TestMode;
import com.razzzil.sphynx.commons.model.wrapper.MetricsModelWrapper;
import com.razzzil.sphynx.commons.constant.StaticsConstants;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MetricsModel {
    private Integer id;
    private Integer iterationId;
    private TestMode testMode;
    private Float timeWithConnections;
    private Float timeWithoutConnections;
    private String additionalMetrics;
    private Integer testExecutionId;
    private LocalDateTime time;
    private Integer userId;
    private Integer testId;
    private Integer process;
    private String message;
    private String iterationName;
    private Integer stepId;

    @SneakyThrows
    private AdditionalMetrics metricsAsObject(){
        return Objects.isNull(additionalMetrics) ? null : StaticsConstants.OM.readValue(additionalMetrics, AdditionalMetrics.class);
    }

    @SneakyThrows
    private void metricsAsObject(AdditionalMetrics additionalMetrics){
        setAdditionalMetrics(Objects.isNull(additionalMetrics) ? null : StaticsConstants.OM.writeValueAsString(additionalMetrics));
    }

    @SneakyThrows
    public static AdditionalMetrics serializeAdditionalMetrics(String json) {
        return StaticsConstants.OM.readValue(json, AdditionalMetrics.class);
    }

    public static MetricsModel fromWrapper(MetricsModelWrapper metricsModelWrapper) {
        return MetricsModel.builder()
                .iterationId(metricsModelWrapper.getIterationId())
          //      .iterations(metricsModelWrapper.getIterations())
                .additionalMetrics(metricsModelWrapper.metricsAsJson())
                .testExecutionId(metricsModelWrapper.getTestExecutionId())
                .testMode(metricsModelWrapper.getTestMode())
                .time(metricsModelWrapper.getTime())
                .timeWithConnections(metricsModelWrapper.getWithConnections())
                .timeWithoutConnections(metricsModelWrapper.getWithoutConnections())
                .userId(metricsModelWrapper.getUserId())
                .message(metricsModelWrapper.getMessage())
                .testId(metricsModelWrapper.getTestId())
                .process(metricsModelWrapper.getProcess())
                .iterationName(metricsModelWrapper.getIterationName())
                .stepId(metricsModelWrapper.getStepId())
                .build();

    }



}

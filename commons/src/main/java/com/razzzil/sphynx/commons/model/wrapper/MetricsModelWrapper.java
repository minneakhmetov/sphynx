package com.razzzil.sphynx.commons.model.wrapper;

import com.razzzil.sphynx.commons.constant.StaticsConstants;
import com.razzzil.sphynx.commons.model.metrics.additional.AdditionalMetrics;
import com.razzzil.sphynx.commons.model.testmode.TestMode;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MetricsModelWrapper {
    private Integer iterationId;
    private Integer testId;
    private TestMode testMode;
    private String iterationName;
    private Float withConnections;
    private Float withoutConnections;
    private AdditionalMetrics metrics;
    private Integer testExecutionId;
    private LocalDateTime time;
    private Integer userId;
    private Boolean clean;
    private Integer stepId;
    private Integer iterationNumber;
    private String message;
    private Integer process;
    private Boolean success;
    private Integer workerId;

    @SneakyThrows
    public String metricsAsJson() {
        return Objects.nonNull(metrics) ? StaticsConstants.OM.writeValueAsString(metrics) : null;
    }

}

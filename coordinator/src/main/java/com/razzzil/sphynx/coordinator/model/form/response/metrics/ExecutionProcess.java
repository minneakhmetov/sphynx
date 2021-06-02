package com.razzzil.sphynx.coordinator.model.form.response.metrics;

import com.razzzil.sphynx.commons.model.metrics.MetricsModel;
import com.razzzil.sphynx.commons.model.wrapper.MetricsModelWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ExecutionProcess {
    private Integer id;
    private Integer process;

    public static ExecutionProcess fromMetricsModel(MetricsModel metricsModel){
        return ExecutionProcess.builder()
                .id(metricsModel.getTestExecutionId())
                .process(metricsModel.getProcess())
                .build();
    }

    public static ExecutionProcess fromMetricsModelWrapper(MetricsModelWrapper metricsModel){
        return ExecutionProcess.builder()
                .id(metricsModel.getTestExecutionId())
                .process(metricsModel.getProcess())
                .build();
    }

}

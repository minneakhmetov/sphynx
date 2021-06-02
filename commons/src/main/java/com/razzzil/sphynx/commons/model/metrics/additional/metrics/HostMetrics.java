package com.razzzil.sphynx.commons.model.metrics.additional.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HostMetrics {
    private Integer cpuPercent;
    private Integer memoryUsed;
    private Integer memoryMax;
}

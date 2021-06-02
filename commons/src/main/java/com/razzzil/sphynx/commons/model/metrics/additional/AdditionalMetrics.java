package com.razzzil.sphynx.commons.model.metrics.additional;

import com.razzzil.sphynx.commons.constant.StaticsConstants;
import com.razzzil.sphynx.commons.model.metrics.additional.metrics.HostMetrics;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AdditionalMetrics {
    private List<HostMetrics> hostMetrics;

}

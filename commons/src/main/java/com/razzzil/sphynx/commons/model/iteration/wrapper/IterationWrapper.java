package com.razzzil.sphynx.commons.model.iteration.wrapper;

import com.razzzil.sphynx.commons.model.iteration.IterationModel;
import com.razzzil.sphynx.commons.model.iteration.configs.IterationConfigs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class IterationWrapper<T extends IterationConfigs> {
    private IterationModel iterationModel;
    private T iterationConfigs;
}

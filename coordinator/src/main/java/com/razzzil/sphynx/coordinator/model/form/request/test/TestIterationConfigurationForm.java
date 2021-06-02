package com.razzzil.sphynx.coordinator.model.form.request.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TestIterationConfigurationForm {
    private TestConfigurationForm test;
    private List<IterationConfigForm> iterations;
}

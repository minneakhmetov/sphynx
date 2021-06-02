package com.razzzil.sphynx.worker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StepIteration {
    private int stepId;
    private int process = 0;

    public int incrementProcess(int process) {
        if (process >= 100) {
            this.process = 100;
            return this.process;
        } else if (process <= this.process) {
            return this.process;
        } else {
            this.process = process;
            return this.process;
        }
    }
}

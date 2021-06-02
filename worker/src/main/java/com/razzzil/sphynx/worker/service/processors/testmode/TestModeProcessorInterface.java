package com.razzzil.sphynx.worker.service.processors.testmode;

import com.razzzil.sphynx.commons.model.database.configs.DatabaseConfigs;
import com.razzzil.sphynx.commons.model.iteration.configs.IterationConfigs;
import com.razzzil.sphynx.commons.model.test.configs.TestConfigs;
import com.razzzil.sphynx.commons.model.testmode.TestMode;
import com.razzzil.sphynx.worker.model.StepResult;
import com.razzzil.sphynx.worker.model.TestConfigsWrapper;
import com.razzzil.sphynx.worker.service.processors.database.DatabaseProcessorInterface;

import javax.sql.DataSource;

public interface TestModeProcessorInterface<X extends TestMode.AbstractTestingModeConfig> {

    <T extends TestConfigs, V extends IterationConfigs, Z extends DatabaseConfigs<? extends DataSource>> StepResult
    process(TestConfigsWrapper<T, V, X, Z> testConfigsWrapper, DatabaseProcessorInterface<T, V, Z> databaseProcessorInterface);
}

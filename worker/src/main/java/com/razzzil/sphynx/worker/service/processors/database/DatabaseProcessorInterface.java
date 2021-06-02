package com.razzzil.sphynx.worker.service.processors.database;

import com.razzzil.sphynx.commons.model.database.DatabaseModel;
import com.razzzil.sphynx.commons.model.database.configs.DatabaseConfigs;
import com.razzzil.sphynx.commons.model.iteration.configs.IterationConfigs;
import com.razzzil.sphynx.commons.model.test.configs.TestConfigs;
import com.razzzil.sphynx.commons.model.testmode.TestMode;
import com.razzzil.sphynx.commons.model.worker.connectionstate.ConnectionState;
import com.razzzil.sphynx.worker.model.ProcessModel;
import com.razzzil.sphynx.worker.model.StepResult;
import com.razzzil.sphynx.worker.model.TestConfigsWrapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface DatabaseProcessorInterface<T extends TestConfigs, V extends IterationConfigs, Z extends DatabaseConfigs<? extends DataSource>> {

    <X extends TestMode.AbstractTestingModeConfig> StepResult process(TestConfigsWrapper<T, V, X, Z> testConfigsWrapper, Integer stepId, Function<ProcessModel, Integer> process, List<Connection> connections);


}

package com.razzzil.sphynx.commons.model.test.wrapper;

import com.razzzil.sphynx.commons.model.test.TestModel;
import com.razzzil.sphynx.commons.model.test.configs.TestConfigs;
import com.razzzil.sphynx.commons.model.testmode.TestMode;

public class TestWrapper<T extends TestConfigs, V extends TestMode.AbstractTestingModeConfig> {
    private TestModel testModel;
    private T testConfigs;
    private V testModeConfigs;
}

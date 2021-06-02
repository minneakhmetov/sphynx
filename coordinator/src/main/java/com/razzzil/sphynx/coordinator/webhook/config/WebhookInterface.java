package com.razzzil.sphynx.coordinator.webhook.config;

import com.razzzil.sphynx.commons.model.execution.TestExecutionModel;
import com.razzzil.sphynx.commons.model.test.TestModel;
import com.razzzil.sphynx.commons.model.webhook.configs.WebhookConfig;

public interface WebhookInterface<T extends WebhookConfig> {

    void process(T config, TestExecutionModel testExecutionModel, TestModel testModel);

}

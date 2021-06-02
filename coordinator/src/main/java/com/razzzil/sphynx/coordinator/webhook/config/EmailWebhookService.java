package com.razzzil.sphynx.coordinator.webhook.config;

import com.razzzil.sphynx.commons.model.execution.TestExecutionModel;
import com.razzzil.sphynx.commons.model.test.TestModel;
import com.razzzil.sphynx.commons.model.webhook.WebhookType;
import com.razzzil.sphynx.commons.model.webhook.configs.EmailWebhookConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component(WebhookType.Names.EMAIL)
public class EmailWebhookService implements WebhookInterface<EmailWebhookConfig> {
    @Override
    public void process(EmailWebhookConfig config, TestExecutionModel testExecutionModel, TestModel testModel) {

    }
}

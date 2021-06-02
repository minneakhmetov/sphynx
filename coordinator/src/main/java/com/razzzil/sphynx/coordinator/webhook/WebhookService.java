package com.razzzil.sphynx.coordinator.webhook;

import com.razzzil.sphynx.commons.model.execution.TestExecutionModel;
import com.razzzil.sphynx.commons.model.test.TestModel;
import com.razzzil.sphynx.commons.model.webhook.WebhookModel;
import com.razzzil.sphynx.commons.model.webhook.WebhookType;
import com.razzzil.sphynx.commons.model.webhook.configs.WebhookConfig;
import com.razzzil.sphynx.coordinator.service.TestIterationWebhookService;
import com.razzzil.sphynx.coordinator.webhook.config.WebhookInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class WebhookService {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TestIterationWebhookService testIterationWebhookService;

    public <T extends WebhookConfig> void process(TestExecutionModel testExecutionModel){
        TestModel testModel = testIterationWebhookService.getTestAndByIdAndUserId(testExecutionModel.getTestConfigsId(), testExecutionModel.getUserId());
        List<WebhookModel> models = testModel.getWebhookModels();
        if (Objects.nonNull(models)) {
            for (WebhookModel webhookModel : models) {
                WebhookInterface<T> webhookInterface = (WebhookInterface<T>) applicationContext.getBean(webhookModel.getWebhookType().name());
                T config = webhookModel.deserializeConfigs();
                webhookInterface.process(config, testExecutionModel, testModel);
            }
        }
    }
}

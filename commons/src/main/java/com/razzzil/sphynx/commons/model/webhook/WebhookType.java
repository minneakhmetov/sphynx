package com.razzzil.sphynx.commons.model.webhook;

import com.fasterxml.jackson.core.type.TypeReference;
import com.razzzil.sphynx.commons.model.test.configs.TestConfigs;
import com.razzzil.sphynx.commons.model.webhook.configs.EmailWebhookConfig;
import com.razzzil.sphynx.commons.model.webhook.configs.TelegramWebhookConfig;
import com.razzzil.sphynx.commons.model.webhook.configs.WebhookConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Type;

@AllArgsConstructor
@Getter
public enum WebhookType {
    TELEGRAM(TelegramWebhookConfig.class),
    EMAIL(EmailWebhookConfig.class);

    public interface Names {
        String TELEGRAM = "TELEGRAM";
        String EMAIL = "EMAIL";
    }

    private Class<? extends WebhookConfig> webhookConfigsClass;

    public <T extends WebhookConfig> TypeReference<T> getTypeReference(){
        return new TypeReference<>() {
            @Override
            public Type getType() {
                return webhookConfigsClass;
            }
        };
    }
}

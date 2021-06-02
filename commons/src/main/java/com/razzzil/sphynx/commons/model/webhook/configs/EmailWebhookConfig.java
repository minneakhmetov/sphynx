package com.razzzil.sphynx.commons.model.webhook.configs;

import com.razzzil.sphynx.commons.model.webhook.WebhookType;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EmailWebhookConfig extends WebhookConfig {
    private String destination;

    @Override
    public WebhookType webhookType() {
        return WebhookType.EMAIL;
    }
}

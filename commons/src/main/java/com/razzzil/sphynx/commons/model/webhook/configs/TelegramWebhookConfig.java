package com.razzzil.sphynx.commons.model.webhook.configs;

import com.razzzil.sphynx.commons.model.webhook.WebhookType;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TelegramWebhookConfig extends WebhookConfig {
    public static final String HOST = "api.telegram.org";
    public static final String PARSE_MODE = "HTML";
    private String chatId;
    private String botId;

    @Override
    public WebhookType webhookType() {
        return WebhookType.TELEGRAM;
    }
}

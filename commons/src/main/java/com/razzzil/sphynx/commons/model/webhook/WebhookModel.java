package com.razzzil.sphynx.commons.model.webhook;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.razzzil.sphynx.commons.model.test.configs.TestConfigs;
import com.razzzil.sphynx.commons.model.type.DatabaseType;
import com.razzzil.sphynx.commons.model.webhook.configs.WebhookConfig;
import lombok.*;

import java.util.Objects;

import static com.razzzil.sphynx.commons.constant.StaticsConstants.OM;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
public class WebhookModel {
    private Integer id;
    private Integer testId;
    private Integer userId;
    private String alias;
    private String configs;
    private WebhookType webhookType;

    @SneakyThrows
    public <T extends WebhookConfig> T deserializeConfigs(Class<T> castClass) {
        return OM.readValue(configs, castClass);
    }

    @SneakyThrows
    public <T extends WebhookConfig> T deserializeConfigs() {
        return OM.readValue(configs, webhookType.getTypeReference());
    }

    @SneakyThrows
    public static <T extends WebhookConfig> T deserializeConfigs(String configs, WebhookType type) {
        return OM.readValue(configs, type.getTypeReference());
    }

//    @SneakyThrows
//    public <T extends TestConfigs> String serializeConfigs(T object) {
//        if (Objects.nonNull(object)) {
//            setDatabaseType(object.databaseType());
//            return OM.writeValueAsString(object);
//        } else return null;
//    }

    @SneakyThrows
    public <T extends WebhookConfig> void setConfigsAsObject(T object) {
        if (Objects.nonNull(object)) {
            setWebhookType(object.webhookType());
            configs = OM.writeValueAsString(object);
        }
    }

    public static WebhookModelBuilder builder() {
        return new WebhookModelBuilder();
    }

    @JsonIgnore
    public boolean isNull(){
        return Objects.isNull(id) && Objects.isNull(testId) && Objects.isNull(userId) && Objects.isNull(alias) && Objects.isNull(configs) && Objects.isNull(webhookType);
    }

    public static final class WebhookModelBuilder {
        private WebhookModel webhookModel;

        private WebhookModelBuilder() {
            webhookModel = new WebhookModel();
        }

        public WebhookModelBuilder id(Integer id) {
            webhookModel.setId(id);
            return this;
        }

        public WebhookModelBuilder testId(Integer testId) {
            webhookModel.setTestId(testId);
            return this;
        }

        public WebhookModelBuilder userId(Integer userId) {
            webhookModel.setUserId(userId);
            return this;
        }

        public WebhookModelBuilder alias(String alias) {
            webhookModel.setAlias(alias);
            return this;
        }

        public WebhookModelBuilder configs(String configs) {
            webhookModel.setConfigs(configs);
            return this;
        }

        public <T extends WebhookConfig> WebhookModelBuilder configs(T object) {
            webhookModel.setConfigsAsObject(object);
            return this;
        }

        public WebhookModelBuilder webhookType(WebhookType webhookType) {
            webhookModel.setWebhookType(webhookType);
            return this;
        }

        public WebhookModel build() {
            return webhookModel;
        }
    }
}

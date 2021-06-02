package com.razzzil.sphynx.coordinator.webhook.config;

import com.razzzil.sphynx.commons.model.execution.TestExecutionModel;
import com.razzzil.sphynx.commons.model.test.TestModel;
import com.razzzil.sphynx.commons.model.webhook.WebhookType;
import com.razzzil.sphynx.commons.model.webhook.configs.EmailWebhookConfig;
import com.razzzil.sphynx.commons.model.webhook.configs.TelegramWebhookConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component(WebhookType.Names.TELEGRAM)
public class TelegramWebhookService implements WebhookInterface<TelegramWebhookConfig> {

    @Autowired
    private RestTemplate restTemplate;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:SS");

    @Override
    public void process(TelegramWebhookConfig config, TestExecutionModel testExecutionModel, TestModel testModel) {

        String message = String.format("Test <b>%s</b> has a state <b>%s</b>. Start time: <b>%s</b>", testModel.getName(), testExecutionModel.getState().name(), testExecutionModel.getStartTime().format(dateTimeFormatter));
        if (Objects.nonNull(testExecutionModel.getEndTime())) {
            long millis = Duration.between(testExecutionModel.getStartTime(), testExecutionModel.getEndTime()).toMillis();
            String duration = String.format("%s:%s:%s",
                    getZero(TimeUnit.MILLISECONDS.toHours(millis)),
                    getZero(TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))),
                    getZero(TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));
            message += String.format(". End time: <b>%s</b>. Elapsed time: <b>%s</b>", testExecutionModel.getEndTime().format(dateTimeFormatter), duration);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(new LinkedMultiValueMap<>(), headers);

        URI uri = UriComponentsBuilder
                .newInstance()
                .scheme("https")
                .host(TelegramWebhookConfig.HOST)
                .pathSegment("bot" + config.getBotId(), "sendMessage")
                .queryParam("text", message)
                .queryParam("chat_id", config.getChatId())
                .queryParam("parse_mode", TelegramWebhookConfig.PARSE_MODE)
                .build()
                .toUri();

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

    }

    private String getZero(long d){
        return d < 10 ? "0" + d : String.valueOf(d);
    }
}

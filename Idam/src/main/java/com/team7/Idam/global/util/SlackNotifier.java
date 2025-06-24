package com.team7.Idam.global.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

@Component
public class SlackNotifier {

    @Value("${slack.webhook-url}")
    private String webhookUrl;

    public void sendMessage(String message) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        String payload = "{\"text\": \"" + message.replace("\"", "\\\"") + "\"}";
        HttpEntity<String> entity = new HttpEntity<>(payload, headers);
        restTemplate.postForObject(webhookUrl, entity, String.class);
    }
}

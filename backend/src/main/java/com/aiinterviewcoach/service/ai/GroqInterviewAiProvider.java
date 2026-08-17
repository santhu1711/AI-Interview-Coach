package com.aiinterviewcoach.service.ai;

import com.aiinterviewcoach.config.AiProperties;
import com.aiinterviewcoach.exception.AiProviderException;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
@ConditionalOnProperty(name = "app.ai.provider", havingValue = "groq", matchIfMissing = true)
public class GroqInterviewAiProvider implements InterviewAiProvider {
    private final WebClient webClient;
    private final AiProperties properties;

    @Autowired
    public GroqInterviewAiProvider(WebClient.Builder webClientBuilder, AiProperties properties) {
        this(webClientBuilder.baseUrl(properties.getBaseUrl()).build(), properties);
    }

    GroqInterviewAiProvider(WebClient webClient, AiProperties properties) {
        this.webClient = webClient;
        this.properties = properties;
    }

    @Override
    public String generate(String prompt) {
        if (properties.getApiKey() == null || properties.getApiKey().isBlank()) {
            throw new AiProviderException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "AI service is not configured. Please try again later.",
                    false);
        }

        Map<String, Object> request = Map.of(
                "model", properties.getModel(),
                "temperature", 0.3,
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(Map.of("role", "user", "content", prompt)));

        String content = webClient.post()
                .uri("/chat/completions")
                .headers(headers -> headers.setBearerAuth(properties.getApiKey()))
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.value() == 401 || status.value() == 403,
                        response -> Mono.error(new AiProviderException(
                                HttpStatus.SERVICE_UNAVAILABLE,
                                "AI provider authentication failed.",
                                false)))
                .onStatus(status -> status.value() == 429,
                        response -> Mono.error(new AiProviderException(
                                HttpStatus.SERVICE_UNAVAILABLE,
                                "AI provider rate limit reached. Please try again shortly.",
                                true)))
                .onStatus(status -> status.is5xxServerError(),
                        response -> Mono.error(new AiProviderException(
                                HttpStatus.SERVICE_UNAVAILABLE,
                                "AI provider is temporarily unavailable.",
                                true)))
                .onStatus(status -> status.isError(),
                        response -> Mono.error(new AiProviderException(
                                HttpStatus.BAD_GATEWAY,
                                "AI provider rejected the request.",
                                false)))
                .bodyToMono(JsonNode.class)
                .map(GroqInterviewAiProvider::contentFrom)
                .timeout(properties.getTimeout())
                .onErrorMap(TimeoutException.class, exception -> new AiProviderException(
                        HttpStatus.GATEWAY_TIMEOUT,
                        "AI provider timed out. Please try again.",
                        true,
                        exception))
                .onErrorMap(WebClientRequestException.class, exception -> new AiProviderException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "AI provider could not be reached. Please try again.",
                        true,
                        exception))
                .onErrorMap(
                        throwable -> !(throwable instanceof AiProviderException),
                        exception -> new AiProviderException(
                                HttpStatus.BAD_GATEWAY,
                                "AI provider returned an invalid response. Please try again.",
                                false,
                                exception))
                .retryWhen(Retry.max(1)
                        .filter(GroqInterviewAiProvider::isRetryable)
                        .onRetryExhaustedThrow((spec, signal) -> signal.failure()))
                .block();

        if (content == null || content.isBlank()) {
            throw new AiProviderException(
                    HttpStatus.BAD_GATEWAY,
                    "AI provider returned an empty response. Please try again.",
                    false);
        }
        return content;
    }

    private static String contentFrom(JsonNode response) {
        JsonNode content = response.path("choices").path(0).path("message").path("content");
        if (!content.isTextual() || content.textValue().isBlank()) {
            throw new AiProviderException(
                    HttpStatus.BAD_GATEWAY,
                    "AI provider returned an empty response. Please try again.",
                    false);
        }
        return content.textValue();
    }

    private static boolean isRetryable(Throwable throwable) {
        return throwable instanceof AiProviderException exception && exception.isRetryable();
    }
}

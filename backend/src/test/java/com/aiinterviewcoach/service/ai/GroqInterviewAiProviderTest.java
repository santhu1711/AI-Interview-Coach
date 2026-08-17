package com.aiinterviewcoach.service.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.aiinterviewcoach.config.AiProperties;
import com.aiinterviewcoach.exception.AiProviderException;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

class GroqInterviewAiProviderTest {
    @Test
    void rejectsMissingApiKeyBeforeMakingARequest() {
        AtomicInteger requests = new AtomicInteger();
        GroqInterviewAiProvider provider = provider(
                request -> {
                    requests.incrementAndGet();
                    return Mono.just(response(HttpStatus.OK, validEnvelope()));
                }, "", Duration.ofSeconds(1));

        assertThatThrownBy(() -> provider.generate("prompt"))
                .isInstanceOf(AiProviderException.class)
                .hasMessage("AI service is not configured. Please try again later.");
        assertThat(requests).hasValue(0);
    }

    @Test
    void retriesRateLimitOnceAndReturnsStructuredContent() {
        AtomicInteger requests = new AtomicInteger();
        GroqInterviewAiProvider provider = provider(
                request -> requests.incrementAndGet() == 1
                        ? Mono.just(response(HttpStatus.TOO_MANY_REQUESTS, "{}"))
                        : Mono.just(response(HttpStatus.OK, validEnvelope())),
                "test-key",
                Duration.ofSeconds(1));

        assertThat(provider.generate("prompt")).contains("questionCategory");
        assertThat(requests).hasValue(2);
    }

    @Test
    void doesNotRetryAuthenticationFailure() {
        AtomicInteger requests = new AtomicInteger();
        GroqInterviewAiProvider provider = provider(
                request -> {
                    requests.incrementAndGet();
                    return Mono.just(response(HttpStatus.UNAUTHORIZED, "{}"));
                },
                "invalid-key",
                Duration.ofSeconds(1));

        assertThatThrownBy(() -> provider.generate("prompt"))
                .isInstanceOf(AiProviderException.class)
                .hasMessage("AI provider authentication failed.");
        assertThat(requests).hasValue(1);
    }

    @Test
    void retriesTimeoutOnceThenReturnsGatewayTimeout() {
        AtomicInteger requests = new AtomicInteger();
        GroqInterviewAiProvider provider = provider(
                request -> {
                    requests.incrementAndGet();
                    return Mono.never();
                },
                "test-key",
                Duration.ofMillis(10));

        assertThatThrownBy(() -> provider.generate("prompt"))
                .isInstanceOf(AiProviderException.class)
                .hasMessage("AI provider timed out. Please try again.");
        assertThat(requests).hasValue(2);
    }

    @Test
    void rejectsEmptyProviderChoice() {
        GroqInterviewAiProvider provider = provider(
                request -> Mono.just(response(HttpStatus.OK, "{\"choices\":[]}")),
                "test-key",
                Duration.ofSeconds(1));

        assertThatThrownBy(() -> provider.generate("prompt"))
                .isInstanceOf(AiProviderException.class)
                .hasMessage("AI provider returned an empty response. Please try again.");
    }

    @Test
    void retriesProviderUnavailableOnce() {
        AtomicInteger requests = new AtomicInteger();
        GroqInterviewAiProvider provider = provider(
                request -> {
                    requests.incrementAndGet();
                    return Mono.just(response(HttpStatus.SERVICE_UNAVAILABLE, "{}"));
                },
                "test-key",
                Duration.ofSeconds(1));

        assertThatThrownBy(() -> provider.generate("prompt"))
                .isInstanceOf(AiProviderException.class)
                .hasMessage("AI provider is temporarily unavailable.");
        assertThat(requests).hasValue(2);
    }

    @Test
    void retriesNetworkFailureOnce() {
        AtomicInteger requests = new AtomicInteger();
        GroqInterviewAiProvider provider = provider(
                request -> {
                    requests.incrementAndGet();
                    return Mono.error(new WebClientRequestException(
                            new IOException("connection failed"),
                            HttpMethod.POST,
                            URI.create("https://api.groq.test/chat/completions"),
                            HttpHeaders.EMPTY));
                },
                "test-key",
                Duration.ofSeconds(1));

        assertThatThrownBy(() -> provider.generate("prompt"))
                .isInstanceOf(AiProviderException.class)
                .hasMessage("AI provider could not be reached. Please try again.");
        assertThat(requests).hasValue(2);
    }

    @Test
    void translatesMalformedProviderEnvelope() {
        GroqInterviewAiProvider provider = provider(
                request -> Mono.just(response(HttpStatus.OK, "not-json")),
                "test-key",
                Duration.ofSeconds(1));

        assertThatThrownBy(() -> provider.generate("prompt"))
                .isInstanceOf(AiProviderException.class)
                .hasMessage("AI provider returned an invalid response. Please try again.");
    }

    private static GroqInterviewAiProvider provider(
            org.springframework.web.reactive.function.client.ExchangeFunction exchangeFunction,
            String apiKey,
            Duration timeout) {
        AiProperties properties = new AiProperties();
        properties.setApiKey(apiKey);
        properties.setTimeout(timeout);
        WebClient client = WebClient.builder().exchangeFunction(exchangeFunction).build();
        return new GroqInterviewAiProvider(client, properties);
    }

    private static ClientResponse response(HttpStatus status, String body) {
        return ClientResponse.create(status)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .build();
    }

    private static String validEnvelope() {
        return """
                {"choices":[{"message":{"content":"{\\"message\\":\\"Question?\\",\\"evaluation\\":\\"NOT_APPLICABLE\\",\\"questionCategory\\":\\"General\\",\\"isFollowUp\\":false,\\"shouldComplete\\":false}"}}]}
                """;
    }
}

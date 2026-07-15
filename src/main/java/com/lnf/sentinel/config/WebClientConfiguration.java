/*
 *
 *  * Copyright © 2024 Lever And Fulcrum Solutions (hereinafter referred to as "LNF").
 *  * All rights reserved.
 *  *
 *  * This source code is the proprietary property of LNF
 *  *
 *  * Unauthorized copying, redistribution, or modification of this code,
 *  * via any medium, is strictly prohibited unless expressly authorized
 *  * in writing by LNF.
 *  *
 *  * This code is confidential and intended solely for the use of LNF
 *  * and its authorized personnel.
 *
 */

package com.lnf.sentinel.config;

//import com.lnf.tenant.core.context.TenantContext;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class WebClientConfiguration {

    private static final Set<HttpMethod> RETRYABLE_METHODS = Set.of(
            HttpMethod.GET, HttpMethod.HEAD,HttpMethod.POST, HttpMethod.OPTIONS, HttpMethod.DELETE, HttpMethod.PUT);

    private static final String RETRY_ATTEMPT_MESSAGE =
            "Retrying call to {} service: {} {} (attempt {}/{}) after transient failure: {}";

    @Value("${audit.service.url}")
    private String auditServiceUrl;
    @Value("${file.service.url}")
    private String fileServiceUrl;
    @Value("${application.maxInMemorySize}")
    private int maxInMemorySize;
    @Value("${connection.timeout}")
    private int timeOut;
    @Value("${lnf.tenant.enabled}")
    private boolean tenantEnabled;

    @Value("${webclient.retry.enabled:true}")
    private boolean retryEnabled;

    @Value("${webclient.retry.max-attempts:3}")
    private int retryMaxAttempts;

    @Value("${webclient.retry.initial-backoff-millis:200}")
    private long retryInitialBackoffMillis;

    @Value("${webclient.retry.max-backoff-millis:2000}")
    private long retryMaxBackoffMillis;

    @Value("${webclient.retry.jitter:0.5}")
    private double retryJitter;

    @Value("${webclient.circuitbreaker.enabled:true}")
    private boolean circuitBreakerEnabled;

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public WebClientConfiguration(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @Bean
    WebClient fileServiceWebClient() {
        return createWebClient(fileServiceUrl,"file");
    }

    @Bean
    WebClient auditWebClient() {
        return createWebClient(auditServiceUrl,"audit");
    }

    private WebClient createWebClient(String baseUrl,String circuitBreakerName) {
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxInMemorySize))
                .build();

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeOut)
                .responseTimeout(Duration.ofMillis(timeOut))
                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(timeOut, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(timeOut, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .filter(retryFilter(circuitBreakerName))
                .filter(circuitBreakerFilter(circuitBreakerName))
                .filter(addTenantHeader())
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(exchangeStrategies)
                .build();
    }

    private ExchangeFilterFunction addTenantHeader() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            if (!tenantEnabled) {
                return Mono.just(clientRequest);
            }

            String currentTenant =null;

            String tenantPrefix = (currentTenant != null && currentTenant.contains("-"))
                    ? currentTenant.split("-")[0]
                    : currentTenant;

            ClientRequest modifiedRequest = ClientRequest.from(clientRequest)
                    .header("X-TenantID", tenantPrefix)
                    .build();

            return Mono.just(modifiedRequest);
        });
    }

    ExchangeFilterFunction circuitBreakerFilter(String name) {
        if (!circuitBreakerEnabled) {
            return (request, next) -> next.exchange(request);
        }
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(name);
        return (request, next) -> next.exchange(request)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }

    ExchangeFilterFunction retryFilter(String serviceName) {
        return (request, next) -> {
            if (!retryEnabled || !RETRYABLE_METHODS.contains(request.method())) {
                return next.exchange(request);
            }

            return next.exchange(request)
                    .flatMap(response -> response.statusCode().is5xxServerError()
                            ? response.createException().flatMap(Mono::error)
                            : Mono.just(response))
                    .retryWhen(Retry.backoff(Math.max(0, retryMaxAttempts - 1),
                                    Duration.ofMillis(retryInitialBackoffMillis))
                            .maxBackoff(Duration.ofMillis(retryMaxBackoffMillis))
                            .jitter(retryJitter)
                            .filter(WebClientConfiguration::isRetryable)
                            .doBeforeRetry(signal -> log.warn(
                                    RETRY_ATTEMPT_MESSAGE,serviceName,
                                    request.method(), request.url(),
                                    signal.totalRetries() + 1, retryMaxAttempts - 1,
                                    signal.failure().toString()))
                            .onRetryExhaustedThrow((spec, signal) -> signal.failure()));
        };
    }

    private static boolean isRetryable(Throwable throwable) {
        if (throwable instanceof WebClientResponseException responseException) {
            log.error("Retry Predicate Exception: {}", throwable.getClass().getName(), throwable);
            return responseException.getStatusCode().is5xxServerError();
        }
        return throwable instanceof WebClientRequestException
                || throwable instanceof TimeoutException
                || throwable instanceof IOException;
    }
}

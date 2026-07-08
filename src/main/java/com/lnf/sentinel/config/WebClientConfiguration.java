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
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfiguration {

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

    @Bean
    WebClient fileServiceWebClient() {
        return createWebClient(fileServiceUrl);
    }

    @Bean
    WebClient auditWebClient() {
        return createWebClient(auditServiceUrl);
    }

    private WebClient createWebClient(String baseUrl) {
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxInMemorySize))
                .build();

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeOut)
                .responseTimeout(Duration.ofMillis(timeOut))
                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(timeOut, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(timeOut, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
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

}

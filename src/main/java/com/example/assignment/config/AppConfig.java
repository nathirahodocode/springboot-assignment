package com.example.assignment.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Value("${external.api.connect-timeout-ms:5000}")
    private int connectTimeoutMs;

    @Value("${external.api.read-timeout-ms:5000}")
    private int readTimeoutMs;

    @Value("${external.api.max-total-connections:100}")
    private int maxTotalConnections;

    @Value("${external.api.max-connections-per-route:20}")
    private int maxConnectionsPerRoute;

    @Bean
    public RestTemplate restTemplate() {
        PoolingHttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnTotal(maxTotalConnections)
                .setMaxConnPerRoute(maxConnectionsPerRoute)
                .build();

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(connectTimeoutMs))
                .setResponseTimeout(Timeout.ofMilliseconds(readTimeoutMs))
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(requestConfig)
                .evictExpiredConnections()
                .build();

        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(factory);
    }
}
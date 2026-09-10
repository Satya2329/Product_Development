package com.farmer.product_development.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class WeatherApiConfig {
    @Value("${openweathermap.api.base-url}")
    private String baseUrl;

    @Bean
    public RestClient weatherRestClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
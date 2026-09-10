package com.farmer.product_development.service;

import com.farmer.product_development.dto.OpenWeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WeatherService {

    private final RestClient restClient;

    @Value("${openweathermap.api.key}")
    private String apiKey;

    public WeatherService(RestClient weatherRestClient) {
        this.restClient = weatherRestClient;
    }


    public OpenWeatherResponse getWeatherByCoordinates(Double lat, Double lon) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/weather")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("appid", apiKey)
                        .queryParam("units", "metric")
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new ResponseStatusException(response.getStatusCode(), "Invalid coordinates or OpenWeatherMap request error");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new ResponseStatusException(response.getStatusCode(), "OpenWeatherMap service is currently unavailable");
                })
                .body(OpenWeatherResponse.class);
    }


    public OpenWeatherResponse getWeatherByCity(String cityName) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/weather")
                        .queryParam("q", cityName)
                        .queryParam("appid", apiKey)
                        .queryParam("units", "metric")
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new ResponseStatusException(response.getStatusCode(), "City not found or invalid request");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new ResponseStatusException(response.getStatusCode(), "OpenWeatherMap service is currently unavailable");
                })
                .body(OpenWeatherResponse.class);
    }
}
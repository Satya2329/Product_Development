package com.farmer.product_development.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenWeatherResponse {

    private String name;
    private Coord coord;
    private Main main;
    private List<Weather> weather;
    private Rain rain;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Coord {
        private Double lon;
        private Double lat;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        private Double temp;
        @JsonProperty("feels_like")
        private Double feelsLike;
        private Integer humidity;
        private Double pressure;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        private Integer id;
        private String main;
        private String description;
        private String icon;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Rain {
        @JsonProperty("1h")
        private Double oneHour = 0.0;

        @JsonProperty("3h")
        private Double threeHour = 0.0;
    }
}
package com.farmer.product_development.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskEvaluationResponse {

    private String locationName;
    private Double latitude;
    private Double longitude;
    private Instant evaluatedAt;

    private WeatherMetrics weatherMetrics;
    private RiskDetail fungalRisk;
    private RiskDetail bacterialRisk;
    private RiskLevel overallRisk;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherMetrics {
        private Double temperatureCelsius;
        private Integer humidityPercent;
        private Double rainfallLastHourMm;
        private String weatherCondition;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskDetail {
        private RiskLevel level;
        private Integer riskScore; // 0 to 100
        private String reasoning;
        private List<String> recommendations;
    }
}
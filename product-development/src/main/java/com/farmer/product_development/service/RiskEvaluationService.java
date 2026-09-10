package com.farmer.product_development.service;

import com.farmer.product_development.dto.OpenWeatherResponse;
import com.farmer.product_development.dto.RiskEvaluationResponse;
import com.farmer.product_development.dto.RiskEvaluationResponse.RiskDetail;
import com.farmer.product_development.dto.RiskEvaluationResponse.WeatherMetrics;
import com.farmer.product_development.dto.RiskLevel;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class RiskEvaluationService {

    private final WeatherService weatherService;

    public RiskEvaluationService(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    public RiskEvaluationResponse evaluateMicroclimateRisk(Double lat, Double lon) {
        OpenWeatherResponse weather = weatherService.getWeatherByCoordinates(lat, lon);
        return calculateRisk(weather);
    }

    public RiskEvaluationResponse evaluateMicroclimateRiskByCity(String cityName) {
        OpenWeatherResponse weather = weatherService.getWeatherByCity(cityName);
        return calculateRisk(weather);
    }

    private RiskEvaluationResponse calculateRisk(OpenWeatherResponse weather) {
        double temp = (weather.getMain() != null && weather.getMain().getTemp() != null)
                ? weather.getMain().getTemp() : 25.0;
        int humidity = (weather.getMain() != null && weather.getMain().getHumidity() != null)
                ? weather.getMain().getHumidity() : 50;
        double rain = (weather.getRain() != null && weather.getRain().getOneHour() != null)
                ? weather.getRain().getOneHour() : 0.0;

        String condition = (weather.getWeather() != null && !weather.getWeather().isEmpty())
                ? weather.getWeather().get(0).getDescription() : "Clear";

        RiskDetail fungalRisk = calculateFungalRisk(temp, humidity, rain);
        RiskDetail bacterialRisk = calculateBacterialRisk(temp, humidity, rain);

        RiskLevel overall = determineOverallRisk(fungalRisk.getLevel(), bacterialRisk.getLevel());

        return RiskEvaluationResponse.builder()
                .locationName(weather.getName())
                .latitude(weather.getCoord() != null ? weather.getCoord().getLat() : null)
                .longitude(weather.getCoord() != null ? weather.getCoord().getLon() : null)
                .evaluatedAt(Instant.now())
                .weatherMetrics(WeatherMetrics.builder()
                        .temperatureCelsius(temp)
                        .humidityPercent(humidity)
                        .rainfallLastHourMm(rain)
                        .weatherCondition(condition)
                        .build())
                .fungalRisk(fungalRisk)
                .bacterialRisk(bacterialRisk)
                .overallRisk(overall)
                .build();
    }

    private RiskDetail calculateFungalRisk(double temp, int humidity, double rain) {
        int score = 10;
        List<String> recs = new ArrayList<>();
        StringBuilder reasons = new StringBuilder();

        if (humidity >= 85) {
            score += 45;
            reasons.append("Sustained relative humidity above 85% strongly favors fungal spore germination. ");
        } else if (humidity >= 70) {
            score += 25;
            reasons.append("Moderate to high humidity provides favorable conditions for fungal growth. ");
        }

        if (temp >= 18.0 && temp <= 28.0) {
            score += 30;
            reasons.append("Temperature (18°C-28°C) is in the optimal fungal incubation range. ");
        } else if (temp > 28.0 && temp <= 32.0) {
            score += 15;
            reasons.append("Sub-optimal temperature slightly slows fungal development. ");
        }

        if (rain > 1.0) {
            score += 15;
            reasons.append("Leaf wetness from recent precipitation increases infection probability. ");
        }

        score = Math.min(score, 100);
        RiskLevel level = getRiskLevelFromScore(score);

        if (level == RiskLevel.HIGH) {
            recs.add("Apply protective bio-fungicide or copper-based spray immediately.");
            recs.add("Improve canopy aeration and pruning to lower microclimate moisture.");
            recs.add("Avoid overhead or sprinkler irrigation.");
        } else if (level == RiskLevel.MODERATE) {
            recs.add("Scout crop lower canopy daily for early leaf spots or powdery mildew.");
            recs.add("Ensure furrow or drip lines are clear to prevent water pooling.");
        } else {
            recs.add("Fungal threat is low. Continue routine field monitoring.");
        }

        return RiskDetail.builder()
                .level(level)
                .riskScore(score)
                .reasoning(reasons.toString().trim())
                .recommendations(recs)
                .build();
    }


    private RiskDetail calculateBacterialRisk(double temp, int humidity, double rain) {
        int score = 10;
        List<String> recs = new ArrayList<>();
        StringBuilder reasons = new StringBuilder();

        if (temp >= 25.0 && temp <= 35.0) {
            score += 35;
            reasons.append("Warm temperatures (25°C-35°C) encourage rapid bacterial multiplication. ");
        }

        if (humidity >= 80) {
            score += 30;
            reasons.append("High humidity maintains open hydathodes and stomata for bacterial entry. ");
        } else if (humidity >= 65) {
            score += 15;
        }

        if (rain > 2.0) {
            score += 25;
            reasons.append("Precipitation splashes bacterial inoculum across neighboring plants. ");
        }

        score = Math.min(score, 100);
        RiskLevel level = getRiskLevelFromScore(score);

        if (level == RiskLevel.HIGH) {
            recs.add("Sterilize farm tools and avoid touching wet foliage to prevent mechanical transmission.");
            recs.add("Apply bactericide (e.g., Streptomycin or Copper Oxychloride) as per local advisory.");
        } else if (level == RiskLevel.MODERATE) {
            recs.add("Inspect foliage for water-soaked lesions or bacterial ooze.");
            recs.add("Maintain proper field drainage.");
        } else {
            recs.add("Bacterial infection risk is low. Maintain clean agricultural practices.");
        }

        return RiskDetail.builder()
                .level(level)
                .riskScore(score)
                .reasoning(reasons.toString().trim())
                .recommendations(recs)
                .build();
    }

    private RiskLevel getRiskLevelFromScore(int score) {
        if (score >= 70) return RiskLevel.HIGH;
        if (score >= 40) return RiskLevel.MODERATE;
        return RiskLevel.LOW;
    }

    private RiskLevel determineOverallRisk(RiskLevel fungal, RiskLevel bacterial) {
        if (fungal == RiskLevel.HIGH || bacterial == RiskLevel.HIGH) {
            return RiskLevel.HIGH;
        }
        if (fungal == RiskLevel.MODERATE || bacterial == RiskLevel.MODERATE) {
            return RiskLevel.MODERATE;
        }
        return RiskLevel.LOW;
    }
}
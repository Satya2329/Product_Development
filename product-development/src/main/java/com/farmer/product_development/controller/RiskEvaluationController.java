package com.farmer.product_development.controller;

import com.farmer.product_development.dto.RiskEvaluationResponse;
import com.farmer.product_development.service.RiskEvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/risk-evaluation")
@CrossOrigin(origins = "http://localhost:4200")
public class RiskEvaluationController {

    private final RiskEvaluationService riskEvaluationService;

    public RiskEvaluationController(RiskEvaluationService riskEvaluationService) {
        this.riskEvaluationService = riskEvaluationService;
    }

    @GetMapping
    public ResponseEntity<RiskEvaluationResponse> getRiskByCoordinates(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) String city) {

        if (city != null && !city.trim().isEmpty()) {
            return ResponseEntity.ok(riskEvaluationService.evaluateMicroclimateRiskByCity(city));
        }

        if (lat == null || lon == null) {
            // Defaulting to Bhubaneswar/Odisha coordinates as a fallback demo location if none provided
            lat = 20.2961;
            lon = 85.8245;
        }

        RiskEvaluationResponse response = riskEvaluationService.evaluateMicroclimateRisk(lat, lon);
        return ResponseEntity.ok(response);
    }
}
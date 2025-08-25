package com.kiwi.features.metrics;

import com.kiwi.types.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/user/metrics")
public class MetricsController {
    private final MetricsService metricsService;
    
    @Autowired
    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }
    
    @PostMapping
    public ResponseEntity<MetricsDTO> createMetrics(@RequestBody MetricsDTO metricsDTO) {
        MetricsDTO savedMetrics = metricsService.createMetric(tryGetJWTEmail(), metricsDTO);
        return ResponseEntity.status(201).body(savedMetrics);
    }
    
    @PutMapping
    public ResponseEntity<MetricsDTO> updateMetrics(@RequestBody MetricsDTO metricsDTO) {
        MetricsDTO savedMetrics = metricsService.updateMetric(tryGetJWTEmail(), metricsDTO);
        return ResponseEntity.ok().body(savedMetrics);
    }
    
    @GetMapping
    public ResponseEntity<MetricsDTO> readMetrics(@RequestParam("date") String date) {
        MetricsDTO savedMetrics = metricsService.getMetrics(tryGetJWTEmail(), LocalDate.parse(date));
        return ResponseEntity.ok().body(savedMetrics);
    }
    
    private Email tryGetJWTEmail() {
        return new Email(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}

package com.kiwi.features.metrics.controllers;

import com.kiwi.features.metrics.data.MetricsDTO;
import com.kiwi.common.types.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<MetricsDTO> createMetrics(@AuthenticationPrincipal UserDetails userDetails, @RequestBody MetricsDTO metricsDTO) {
        MetricsDTO savedMetrics = metricsService.createMetric(new Email(userDetails.getUsername()), metricsDTO);
        return ResponseEntity.status(201).body(savedMetrics);
    }
    
    @PutMapping
    public ResponseEntity<MetricsDTO> updateMetrics(@AuthenticationPrincipal UserDetails userDetails, @RequestBody MetricsDTO metricsDTO) {
        MetricsDTO savedMetrics = metricsService.updateMetric(new Email(userDetails.getUsername()), metricsDTO);
        return ResponseEntity.ok().body(savedMetrics);
    }
    
    @GetMapping
    public ResponseEntity<MetricsDTO> readMetrics(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("date") String date) {
        MetricsDTO savedMetrics = metricsService.getMetrics(new Email(userDetails.getUsername()), LocalDate.parse(date));
        return ResponseEntity.ok().body(savedMetrics);
    }
}

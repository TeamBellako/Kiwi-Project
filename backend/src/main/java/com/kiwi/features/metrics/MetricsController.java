package com.kiwi.features.metrics;

import com.kiwi.types.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/user/metrics")
public class MetricsController {
    private final MetricsService metricsService;
    
    @Autowired
    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }
    
    @PostMapping
    public ResponseEntity<String> createMetrics(@RequestBody MetricsDTO metricsDTO) {
        Email jwtEmail = tryGetJWTEmail();
        
        metricsService.createMetric(jwtEmail, metricsDTO);
        URI location = URI.create("/api/user/metrics/" + jwtEmail.value() + "/" + metricsDTO.getDate());

        return ResponseEntity.created(location).build();
    }
    
    @PutMapping
    public ResponseEntity<String> updateMetrics(@RequestBody MetricsDTO metricsDTO) {
        metricsService.updateMetric(tryGetJWTEmail(), metricsDTO);
        
        return ResponseEntity.ok().body("Metrics updated");
    }
    
    @GetMapping
    public ResponseEntity<Optional<MetricsDTO>> readMetrics(@RequestParam("date") String date) {
        return ResponseEntity.ok().body(metricsService.getMetrics(tryGetJWTEmail(), LocalDate.parse(date)));
    }
    
    private Email tryGetJWTEmail() {
        return new Email(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}

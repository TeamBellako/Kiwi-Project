package com.kiwi.features.metrics;

import com.kiwi.types.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
    public ResponseEntity<String> createMetrics(@RequestBody MetricsDTO metricsDTO) {
        String jwtEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Email email = new Email(jwtEmail); 
        
        metricsService.createMetric(metricsDTO);
        URI location = URI.create("/api/user/metrics/" + email.value() + "/" + metricsDTO.getDate());

        return ResponseEntity.created(location).build();
    }
    
    @PutMapping
    public ResponseEntity<String> updateMetrics(@RequestBody MetricsDTO metricsDTO) {
        metricsService.updateMetric(metricsDTO);
        
        return ResponseEntity.ok().body("Metrics updated");
    }
    
    @GetMapping
    public ResponseEntity<MetricsDTO> getMetricsByDate(@RequestParam("date") String date) {
        String jwtEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        
        return metricsService.getMetricsByEmailAndDate(new Email(jwtEmail), LocalDate.parse(date))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

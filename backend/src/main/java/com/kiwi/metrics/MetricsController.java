package com.kiwi.metrics;

import com.kiwi.users.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
        if (isUserImpersonating(metricsDTO.getEmail())) 
            return ResponseEntity.status(401).body("You can only create metrics with your own email");
        
        metricsService.createMetric(metricsDTO);
        URI location = URI.create("/api/user/metrics/" + metricsDTO.getEmail() + "/" + metricsDTO.getDate());

        return ResponseEntity.created(location).build();
    }
    
    @PutMapping
    public ResponseEntity<String> updateMetrics(@RequestBody MetricsDTO metricsDTO) {
        if (isUserImpersonating(metricsDTO.getEmail())) 
            return ResponseEntity.status(401).body("You can only update your own metrics");
        
        metricsService.updateMetric(metricsDTO);
        
        return ResponseEntity.ok().body("Metrics updated");
    }
    
    @GetMapping
    public ResponseEntity<MetricsDTO> getMetrics(
            @RequestParam("email") String email,
            @RequestParam("date") String date
    ) {
        if (isUserImpersonating(email)) return ResponseEntity.status(401).body(null);

        return metricsService.getMetricsByEmailAndDate(new Email(email), LocalDate.parse(date))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    private boolean isUserImpersonating(String incomingEmail) {
        String jwtEmail = SecurityContextHolder.getContext().getAuthentication().getName(); 
        return !jwtEmail.equals(incomingEmail);
    }
}

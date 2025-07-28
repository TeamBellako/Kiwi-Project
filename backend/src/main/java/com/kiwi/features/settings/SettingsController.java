package com.kiwi.features.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/settings")
public class SettingsController {
    private final SettingsService settingsService;

    @Autowired
    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping
    public ResponseEntity<SettingsDTO> getSettings() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        
        return settingsService.getSettingsByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<SettingsDTO> updateSettings(@RequestBody SettingsDTO settingsDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!settingsDTO.getEmail().equals(email)) return ResponseEntity.badRequest().build(); 
        
        SettingsDTO updatedSettingsDTO = settingsService.updateSettings(settingsDTO);

        return ResponseEntity.ok(updatedSettingsDTO);
    }
}

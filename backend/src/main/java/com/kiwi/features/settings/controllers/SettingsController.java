package com.kiwi.features.settings.controllers;

import com.kiwi.features.settings.data.SettingsDTO;
import com.kiwi.features.settings.data.SettingsDataMapper;
import com.kiwi.features.settings.data.SettingsPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<SettingsDTO> getSettings(@AuthenticationPrincipal UserDetails userDetails) {
        SettingsPersistence settingsPersistence = settingsService.getSettings(userDetails.getUsername());
        return ResponseEntity.ok(SettingsDataMapper.toDTO(settingsPersistence));
    }

    @PutMapping
    public ResponseEntity<SettingsDTO> updateSettings(@AuthenticationPrincipal UserDetails userDetails, @RequestBody SettingsDTO settingsDTO) {
        return ResponseEntity.ok(settingsService.updateSettings(userDetails.getUsername(), settingsDTO));
    }
}

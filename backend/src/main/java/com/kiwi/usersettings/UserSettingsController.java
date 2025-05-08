package com.kiwi.usersettings;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/settings")
public class UserSettingsController {
    private final UserSettingsService userSettingsService;

    @Autowired
    public UserSettingsController(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserSettingsDTO> getUserSettings() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userSettingsService.getUserSettingsByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<UserSettingsDTO> updateUserSettings(@RequestBody @Valid UserSettingsDTO userSettingsDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!userSettingsDTO.getEmail().equals(email)) return ResponseEntity.badRequest().build(); 
        
        UserSettingsDTO updatedUserSettingsDTO = userSettingsService.updateUserSettings(userSettingsDTO);

        return ResponseEntity.ok(updatedUserSettingsDTO);
    }
}

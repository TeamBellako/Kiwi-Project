package com.kiwi.usersettings;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @GetMapping
    public ResponseEntity<UserSettingsDTO> getUserSettings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String email = authentication.getName();
        
        return userSettingsService.getUserSettingsByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<UserSettingsDTO> updateUserSettings(@RequestBody @Valid UserSettingsDTO userSettingsDTO) {
        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!userSettingsDTO.getEmail().equals(email)) return ResponseEntity.badRequest().build(); 
        
        UserSettingsDTO updatedUserSettingsDTO = userSettingsService.updateUserSettings(userSettingsDTO);

        return ResponseEntity.ok(updatedUserSettingsDTO);
    }
}

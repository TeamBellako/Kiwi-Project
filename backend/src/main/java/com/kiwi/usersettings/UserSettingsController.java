package com.kiwi.usersettings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("api/settings")
public class UserSettingsController {

    private final UserSettingsService userSettingsService;

    @Autowired
    public UserSettingsController(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }

    
    @PostMapping
    public ResponseEntity<UserSettings> createUserSettings(@RequestBody UserSettings userSettings) {
        UserSettings createdUserSettings = userSettingsService.createUserSettings(userSettings);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUserSettings.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdUserSettings);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserSettings> getUserSettingsById(@PathVariable Integer id) {
        Optional<UserSettings> userSettings = userSettingsService.getUserSettingsById(id);

        return userSettings
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping
    public ResponseEntity<UserSettings> updateUserSettings(@RequestBody UserSettings userSettings) {
        UserSettings updatedUserSettings = userSettingsService.updateUserSettings(userSettings);
        
        return ResponseEntity.ok(updatedUserSettings);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUserSettings(@PathVariable Integer id) {
        userSettingsService.deleteUserSettings(id);
        
        return ResponseEntity.noContent().build();
    }
}
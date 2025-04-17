package com.kiwi.controller;

import com.kiwi.entity.UserSettings;
import com.kiwi.service.UserSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

import static java.util.Collections.singletonMap;

@RestController
@RequestMapping("api/settings")
public class UserSettingsController {
    
    private final UserSettingsService userSettingsService;

    @Autowired
    public UserSettingsController(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }


    @PostMapping
    public ResponseEntity<Map<String, UserSettings>> createUserSettings(@RequestBody UserSettings userSettings) {
        UserSettings createdUserSettings = userSettingsService.createUserSettings(userSettings);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUserSettings.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(singletonMap("userSettings", createdUserSettings));
    }

    @GetMapping("{id}")
    public ResponseEntity<Map<String, UserSettings>> getUserSettingsById(@PathVariable Integer id) {
        return userSettingsService.getUserSettingsById(id)
                .map(userSettings -> ResponseEntity.ok(singletonMap("userSettings", userSettings)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(singletonMap("userSettings", null)));
    }

    @PutMapping
    public ResponseEntity<Map<String, UserSettings>> updateUserSettings(@RequestBody UserSettings userSettings) {
        return ResponseEntity.ok(singletonMap("userSettings", userSettingsService.updateUserSettings(userSettings)));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUserSettings(@PathVariable Integer id) {
        userSettingsService.deleteUserSettings(id);
        return ResponseEntity.noContent().build();
    }
}

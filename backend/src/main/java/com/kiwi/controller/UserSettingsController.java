package com.kiwi.controller;

import com.kiwi.entity.UserSettings;
import com.kiwi.service.UserSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("api/settings")
public class UserSettingsController {
    private final UserSettingsService userSettingsService;

    @Autowired
    public UserSettingsController(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }

    @PostMapping
    public ResponseEntity<String> createUserSettings(@RequestBody UserSettings userSettings) {
        return null;
    }

    @GetMapping("{id}")
    public ResponseEntity<Map<String, UserSettings>> getUserSettingsById(@PathVariable Integer id) {
        return userSettingsService.getUserSettingsById(id)
                .map(userSettings -> ResponseEntity.ok(Collections.singletonMap("userSettings", userSettings)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("userSettings", null)));
    }

    @PutMapping
    public ResponseEntity<String> updateUserSettings(@RequestBody UserSettings userSettings) {
        return null;
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUserSettings(@PathVariable Integer id) {
        return null;
    }
}

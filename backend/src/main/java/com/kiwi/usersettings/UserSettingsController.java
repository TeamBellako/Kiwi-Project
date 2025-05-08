package com.kiwi.usersettings;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<UserSettingsDTO> createUserSettings(@RequestBody @Valid UserSettingsDTO userSettingsDTO) {
        UserSettingsDTO createdUserSettingsDTO = userSettingsService.createUserSettings(userSettingsDTO);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUserSettingsDTO.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdUserSettingsDTO);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserSettingsDTO> getUserSettingsById(@PathVariable Integer id) {
        Optional<UserSettingsDTO> userSettingsDTO = userSettingsService.getUserSettingsById(id);

        return userSettingsDTO
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/me")
    public ResponseEntity<UserSettingsDTO> getMyUserSettings() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        
        return userSettingsService.getUserSettingsByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<UserSettingsDTO> updateUserSettings(@RequestBody @Valid UserSettingsDTO userSettingsDTO) {
        UserSettingsDTO updatedUserSettingsDTO = userSettingsService.updateUserSettings(userSettingsDTO);

        return ResponseEntity.ok(updatedUserSettingsDTO);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUserSettings(@PathVariable Integer id) {
        userSettingsService.deleteUserSettings(id);

        return ResponseEntity.noContent().build();
    }
}

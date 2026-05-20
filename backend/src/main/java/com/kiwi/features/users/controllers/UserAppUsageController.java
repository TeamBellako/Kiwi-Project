package com.kiwi.features.users.controllers;

import com.kiwi.features.users.data.UserAppUsageDTO;
import com.kiwi.common.types.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/app-usage")
public class UserAppUsageController {
    private final UserAppUsageService service;

    @Autowired
    public UserAppUsageController(UserAppUsageService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserAppUsageDTO> saveBaseline(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserAppUsageDTO dto
    ) {
        UserAppUsageDTO saved = service.saveBaselineIfNotExists(new Email(userDetails.getUsername()), dto);
        return ResponseEntity.status(201).body(saved);
    }
}

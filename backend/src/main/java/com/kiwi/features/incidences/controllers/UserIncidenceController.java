package com.kiwi.features.incidences.controllers;

import com.kiwi.common.types.Email;
import com.kiwi.features.incidences.data.UserIncidenceDTO;
import com.kiwi.features.users.controllers.UsersService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user_incidences")
public class UserIncidenceController {
    
    private final UserIncidenceService userIncidenceService;
    private final UsersService usersService;

    public UserIncidenceController(UserIncidenceService userIncidenceService, UsersService usersService) {
        this.userIncidenceService = userIncidenceService;
        this.usersService = usersService;
    }

    @PostMapping()
    public ResponseEntity<Void> updateOrCreateUserIncidence(
            @AuthenticationPrincipal @NotNull UserDetails userDetails,
            @RequestBody UserIncidenceDTO dto
    ) {
        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();

        userIncidenceService.updateOrCreateUserIncidence(
                userId,
                dto
        );

        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{incidenceName}")
    public ResponseEntity<Boolean> getUserIncidence(@AuthenticationPrincipal @NotNull UserDetails userDetails, @PathVariable String incidenceName) {
        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();
        
        return ResponseEntity.ok(userIncidenceService.getUserIncidence(userId, incidenceName));
    }
}

package com.kiwi.features.users.controllers;

import com.kiwi.features.users.data.UserPointsDTO;
import com.kiwi.common.types.Email;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UsersProtectedController {
    private final UsersService usersService;

    public UsersProtectedController(UsersService usersService) {
        this.usersService = usersService;
    }

    /**
     * Obtiene los puntos del usuario autenticado.
     * GET /api/user/points
     */
    @GetMapping("/points")
    public ResponseEntity<UserPointsDTO> getUserPoints(Authentication authentication) {
        String email = authentication.getName();
        Optional<UserPointsDTO> userPoints = usersService.getUserPoints(new Email(email));
        
        return userPoints
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

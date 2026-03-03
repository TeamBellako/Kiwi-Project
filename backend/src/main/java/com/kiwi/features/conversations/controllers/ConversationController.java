package com.kiwi.features.conversations.controllers;

import com.kiwi.common.types.Email;
import com.kiwi.features.conversations.data.ConversationDTO;
import com.kiwi.features.users.controllers.UsersService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {
    
    private final ConversationService conversationService;
    private final UsersService usersService;
    
    public ConversationController(ConversationService conversationService, UsersService usersService) {
        this.conversationService = conversationService;
        this.usersService = usersService;
    }
    
    /**
     * GET /api/conversations/{id}
     * Obtener una conversation por ID con sus options.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConversationDTO> getConversationById(@PathVariable Long id) {
        ConversationDTO conversation = conversationService.getConversationById(id);
        return ResponseEntity.ok(conversation);
    }

    /**
     * POST /api/conversations/options
     * Guardar la selección de opciones de conversación de un usuario.
     */
    @PostMapping("/options")
    public ResponseEntity<Void> saveConversationOptions(
            @RequestBody List<Long> optionIds,
            @AuthenticationPrincipal @NotNull UserDetails userDetails
    ) {
        Long userId = usersService.getUserByEmail(new Email(userDetails.getUsername()))
                .orElseThrow()
                .getId();
        conversationService.saveConversationOptions(userId, optionIds);
        return ResponseEntity.ok().build();
    }
}

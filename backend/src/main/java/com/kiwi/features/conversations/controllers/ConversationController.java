package com.kiwi.features.conversations.controllers;

import com.kiwi.features.conversations.data.ConversationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {
    
    private final ConversationService conversationService;
    
    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
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
}

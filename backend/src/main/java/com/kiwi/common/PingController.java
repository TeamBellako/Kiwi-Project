package com.kiwi.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PingController {
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
    
    @GetMapping("/public/ping")
    public ResponseEntity<String> publicPing() {
        return ResponseEntity.ok("pong");
    }

    @GetMapping("/user/ping")
    public ResponseEntity<String> userPing() {
        return ResponseEntity.ok("pong");
    }

    @GetMapping("/admin/ping")
    public ResponseEntity<String> adminPing() {
        return ResponseEntity.ok("pong");
    }
}
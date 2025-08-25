package com.kiwi.utils;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.kiwi.utils.HTTPUtils.createSuccessResponseBody;

@RestController
@RequestMapping("/api")
public class PingController {
    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(createSuccessResponseBody("pong"));
    }
    
    @GetMapping("/public/ping")
    public ResponseEntity<Map<String, String>> publicPing() {
        return ResponseEntity.ok(createSuccessResponseBody("pong"));
    }

    @GetMapping("/user/ping")
    public ResponseEntity<Map<String, String>> userPing() {
        return ResponseEntity.ok(createSuccessResponseBody("pong"));
    }

    @GetMapping("/admin/ping")
    public ResponseEntity<Map<String, String>> adminPing() {
        return ResponseEntity.ok(createSuccessResponseBody("pong"));
    }
}
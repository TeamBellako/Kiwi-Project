package com.kiwi.controller;

import com.kiwi.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("api/hello")
public class HelloController {
    @Autowired
    private HelloService helloService;

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, String>> getMessageById(@PathVariable int id) {
        try {
            String message = helloService.getMessageById(id);
            return ResponseEntity.ok(Collections.singletonMap("message", message));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "Message not found"));
        }
    }
}

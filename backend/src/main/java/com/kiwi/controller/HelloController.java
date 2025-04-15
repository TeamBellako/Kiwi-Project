package com.kiwi.controller;

import com.kiwi.entity.HelloDB;
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
import java.util.Optional;

@RestController
@RequestMapping("api/hello")
public class HelloController {
    private HelloService helloService;

    @Autowired
    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, String>> getMessageById(@PathVariable Integer id) {
        return helloService.getMessageById(id)
                .map(msg -> ResponseEntity.ok(Collections.singletonMap("message", msg.getMessage())))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Message not found")));
    }
}

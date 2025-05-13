package com.kiwi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenGenerator implements CommandLineRunner {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void run(String... args) throws Exception {
        // Use a test username to generate a token
        String username = "finn@thehuman.com";
        String token = jwtUtils.generateToken(username);

        // Print out the generated token (you can use it in Postman)
        System.out.println("Generated JWT Token: " + token);
    }
}

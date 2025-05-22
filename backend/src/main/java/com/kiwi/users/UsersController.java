package com.kiwi.users;

import com.kiwi.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/public")
public class UsersController {
    private final UsersService usersService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersController(UsersService usersService, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.usersService = usersService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }
    
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody LoginDTO loginDTO) {
        UsersDTO newUserDTO = new UsersDTO(loginDTO.getEmail(), loginDTO.getPassword());
        usersService.createUser(newUserDTO);
        return ResponseEntity.status(201).body("");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDTO loginDTO) {
        Email providedEmail = new Email(loginDTO.getEmail());
        Password providedPassword = new Password(loginDTO.getPassword());

        Optional<UsersPersistence> userPersistenceOpt = usersService.getUserByEmail(providedEmail);
        if (userPersistenceOpt.isEmpty()) throw new UsersNotFoundException(providedEmail.value());

        UsersPersistence userPersistence = userPersistenceOpt.get();

        boolean isPasswordCorrect = passwordEncoder.matches(providedPassword.value(), userPersistence.getPassword());

        if (isPasswordCorrect) {
            Map<String, String> response = new HashMap<>();
            response.put("jwt", jwtUtils.generateToken(userPersistence.getEmail().value()));
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Incorrect email or password"));
        }
    }

}

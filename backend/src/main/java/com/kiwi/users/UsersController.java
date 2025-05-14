package com.kiwi.users;

import com.kiwi.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/public")
public class UsersController {
    private final UsersService usersService;
    private final JwtUtils jwtUtils;

    @Autowired
    public UsersController(UsersService usersService, JwtUtils jwtUtils) {
        this.usersService = usersService;
        this.jwtUtils = jwtUtils;
    }
    
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UsersDTO userDTO) {
        return ResponseEntity.status(500).body("Not implemented");
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDTO loginDTO) {
        Email providedEmail = new Email(loginDTO.getEmail());
        Password providedPassword = new Password(loginDTO.getPassword());
        
        Optional<UsersDTO> internalUserDTO = usersService.getUserByEmail(providedEmail);
        if (internalUserDTO.isEmpty()) throw new UsersNotFoundException(providedEmail.value());
        
        Users internalUser = internalUserDTO.get().toDomainObject();
        boolean isPasswordCorrect = providedPassword.equals(internalUser.getPassword());

        if (isPasswordCorrect) {
            Map<String, String> response = new HashMap<>();
            response.put("jwt", jwtUtils.generateToken(internalUser.getEmail().value()));
            return ResponseEntity.status(200).body(response);
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Incorrect password"));
        }
    }
}

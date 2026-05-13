package com.kiwi.features.users.controllers;

import com.kiwi.features.users.data.LoggedDTO;
import com.kiwi.features.users.data.LoginDTO;
import com.kiwi.features.users.exceptions.LoginUserInvalidException;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.security.JwtUtils;
import com.kiwi.common.types.Email;
import com.kiwi.common.types.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

import static com.kiwi.common.utils.FormatUtils.formatDate;
import static com.kiwi.common.utils.HTTPUtils.createSuccessResponseBody;

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
    public ResponseEntity<Map<String, String>> signup(@RequestBody LoginDTO loginDTO) {
        usersService.createUser(loginDTO);
        return ResponseEntity.status(201).body(createSuccessResponseBody("Created successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoggedDTO> login(@RequestBody LoginDTO loginDTO) {
        Email providedEmail;
        Password providedPassword;
        try {
            providedEmail = new Email(loginDTO.getEmail());
            providedPassword = new Password(loginDTO.getPassword());
        } catch (IllegalArgumentException e) {
            throw new LoginUserInvalidException();
        }

        Optional<UsersPersistence> userPersistenceOpt = usersService.getUserByEmail(providedEmail);
        if (userPersistenceOpt.isEmpty()) throw new LoginUserInvalidException();

        UsersPersistence userPersistence = userPersistenceOpt.get();

        boolean isPasswordCorrect = passwordEncoder.matches(providedPassword.value(), userPersistence.getHashedPassword());

        if (isPasswordCorrect) {
            LoggedDTO response = new LoggedDTO(jwtUtils.generateToken(userPersistence.getEmail()), formatDate(userPersistence.getRegisterDate()));
            return ResponseEntity.ok(response);
        } else {
            throw new LoginUserInvalidException();
        }
    }

}

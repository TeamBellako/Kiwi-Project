package com.kiwi.features.users.controllers;

import com.kiwi.features.users.data.LoggedDTO;
import com.kiwi.features.users.data.LanguagePreferenceDTO;
import com.kiwi.features.users.data.LoginDTO;
import com.kiwi.features.users.exceptions.LoginUserInvalidException;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.security.JwtUtils;
import com.kiwi.common.types.Email;
import com.kiwi.common.types.Password;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PutMapping;
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
    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

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
        logger.info("Signup endpoint received a request");
        usersService.createUser(loginDTO);
        logger.info("Signup endpoint completed successfully");
        return ResponseEntity.status(201).body(createSuccessResponseBody("Created successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoggedDTO> login(@RequestBody LoginDTO loginDTO) {
        logger.info("Login endpoint received a request");
        Email providedEmail;
        Password providedPassword;
        try {
            providedEmail = new Email(loginDTO.getEmail());
            providedPassword = new Password(loginDTO.getPassword());
        } catch (IllegalArgumentException e) {
            logger.warn("Login rejected during credential format validation");
            throw new LoginUserInvalidException();
        }

        Optional<UsersPersistence> userPersistenceOpt = usersService.getUserByEmail(providedEmail);
        if (userPersistenceOpt.isEmpty()) {
            logger.warn("Login account lookup found no matching user");
            throw new LoginUserInvalidException();
        }

        UsersPersistence userPersistence = userPersistenceOpt.get();
        logger.info("Login account lookup succeeded; checking password");

        boolean isPasswordCorrect = passwordEncoder.matches(providedPassword.value(), userPersistence.getHashedPassword());

        if (isPasswordCorrect) {
            logger.info("Login password matched; generating JWT");
            LoggedDTO response = new LoggedDTO(jwtUtils.generateToken(userPersistence.getEmail()), formatDate(userPersistence.getRegisterDate()));
            logger.info("Login endpoint completed successfully");
            return ResponseEntity.ok(response);
        } else {
            logger.warn("Login rejected because the password did not match");
            throw new LoginUserInvalidException();
        }
    }

    @RestController
    @RequestMapping("/api/user/language")
    public static class LanguageController {
        private final UsersService usersService;

        public LanguageController(UsersService usersService) {
            this.usersService = usersService;
        }

        @PutMapping
        public ResponseEntity<Void> updateLanguage(
                @Valid @RequestBody LanguagePreferenceDTO request,
                Authentication authentication) {
            System.out.println("Updating language for user: " + authentication.getName());
            usersService.updateUserLanguage(authentication.getName(), request.language());
            return ResponseEntity.noContent().build();
        }
    }
}

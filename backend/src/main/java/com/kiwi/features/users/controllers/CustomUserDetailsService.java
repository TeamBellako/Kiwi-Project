package com.kiwi.features.users.controllers;

import com.kiwi.features.localization.LocalizationService;
import com.kiwi.features.users.data.LanguageAwareUserDetails;
import com.kiwi.features.users.data.UsersPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService  implements UserDetailsService {
    private final UsersRepository usersRepository;
    
    @Autowired
    public CustomUserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UsersPersistence> usersPersistence = usersRepository.findByEmail(username);
        if (usersPersistence.isEmpty()) throw new UsernameNotFoundException("User Not Found with username: " + username);

        UsersPersistence user = usersPersistence.get();
        return new LanguageAwareUserDetails(
                user.getEmail(),
                user.getHashedPassword(),
                LocalizationService.normalizeLanguagePreference(user.getLanguagePreference())
        );
    }
}

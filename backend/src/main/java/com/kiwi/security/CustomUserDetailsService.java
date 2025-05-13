package com.kiwi.security;

import com.kiwi.users.UsersPersistence;
import com.kiwi.users.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class CustomUserDetailsService  implements UserDetailsService {
    private final UsersRepository usersRepository;
    
    @Autowired
    public CustomUserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsersPersistence usersPersistence = usersRepository.findByEmail(username);
        
        if (usersPersistence == null) 
            throw new UsernameNotFoundException("User Not Found with username: " + username);
        
        return new User(
                usersPersistence.getEmail().value(),
                usersPersistence.getPassword().value(),
                Collections.emptyList()
        );
    }
}

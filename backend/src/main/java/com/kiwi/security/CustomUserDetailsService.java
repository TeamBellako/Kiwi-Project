package com.kiwi.security;

import com.kiwi.users.Users;
import com.kiwi.users.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class CustomUserDetailsService  implements UserDetailsService {
    @Autowired
    private UsersRepository usersRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = usersRepository.findByEmail(username);
        
        if (users == null) 
            throw new UsernameNotFoundException("Users Not Found with username: " + username);
        
        return new org.springframework.security.core.userdetails.User(
                users.getEmail(),
                users.getPassword(),
                Collections.emptyList()
        );
    }
}

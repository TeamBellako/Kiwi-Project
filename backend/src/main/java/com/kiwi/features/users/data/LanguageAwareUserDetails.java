package com.kiwi.features.users.data;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public final class LanguageAwareUserDetails implements UserDetails {

    private final UserDetails userDetails;
    private final String languagePreference;

    public LanguageAwareUserDetails(String username, String password, String languagePreference) {
        this.userDetails = User.withUsername(username)
                .password(password)
                .authorities(List.of())
                .build();
        this.languagePreference = languagePreference;
    }

    public String getLanguagePreference() {
        return languagePreference;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userDetails.getAuthorities();
    }

    @Override
    public String getPassword() {
        return userDetails.getPassword();
    }

    @Override
    public String getUsername() {
        return userDetails.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return userDetails.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return userDetails.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return userDetails.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return userDetails.isEnabled();
    }
}

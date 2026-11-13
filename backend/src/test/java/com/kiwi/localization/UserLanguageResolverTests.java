package com.kiwi.localization;

import com.kiwi.features.localization.UserLanguageResolver;
import com.kiwi.features.users.controllers.CustomUserDetailsService;
import com.kiwi.features.users.controllers.UsersRepository;
import com.kiwi.features.users.data.LanguageAwareUserDetails;
import com.kiwi.features.users.data.UsersPersistence;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserLanguageResolverTests {

    @Mock
    private UsersRepository usersRepository;

    @After
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void resolve_usesLanguageLoadedForAuthenticationAndSeesChangesOnNextLoad() {
        UsersPersistence user = UsersPersistence.builder()
                .email("user@example.com")
                .hashedPassword("hashed-password")
                .registerDate(LocalDate.now())
                .languagePreference("en")
                .build();
        when(usersRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        CustomUserDetailsService userDetailsService = new CustomUserDetailsService(usersRepository);
        UserDetails englishUser = userDetailsService.loadUserByUsername(user.getEmail());

        assertTrue(englishUser instanceof LanguageAwareUserDetails);
        setAuthentication(englishUser);
        assertEquals("en", UserLanguageResolver.resolve(usersRepository));

        user.setLanguagePreference("es");
        UserDetails spanishUser = userDetailsService.loadUserByUsername(user.getEmail());
        setAuthentication(spanishUser);
        assertEquals("es", UserLanguageResolver.resolve(usersRepository));

        verify(usersRepository, times(2)).findByEmail(user.getEmail());
    }

    private void setAuthentication(UserDetails userDetails) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
    }
}

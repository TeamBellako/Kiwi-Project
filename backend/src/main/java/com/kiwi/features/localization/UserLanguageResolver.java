package com.kiwi.features.localization;

import com.kiwi.features.users.controllers.UsersRepository;
import com.kiwi.features.users.data.LanguageAwareUserDetails;
import com.kiwi.features.users.data.UsersPersistence;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class UserLanguageResolver {

    private UserLanguageResolver() {
    }

    public static String resolve(UsersRepository usersRepository) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return "en";
        }

        if (authentication.getPrincipal() instanceof LanguageAwareUserDetails userDetails) {
            return LocalizationService.normalizeLanguagePreference(userDetails.getLanguagePreference());
        }

        return usersRepository.findByEmail(authentication.getName())
                .map(UsersPersistence::getLanguagePreference)
                .map(LocalizationService::normalizeLanguagePreference)
                .orElse("en");
    }
}

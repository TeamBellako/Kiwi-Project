package com.kiwi.features.users.data;

import jakarta.validation.constraints.NotBlank;

public record LanguagePreferenceDTO(@NotBlank String language) {
}
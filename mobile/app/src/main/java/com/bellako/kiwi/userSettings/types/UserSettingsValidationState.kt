package com.bellako.kiwi.userSettings.types

data class UserSettingsValidationState(
    val emailError: String? = null,
    val generalError: String? = null
)
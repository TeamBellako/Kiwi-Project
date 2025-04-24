package com.bellako.kiwi.usersettings

data class UserSettingsState(
    val email: String = "",
    val areNotificationsEnabled: Boolean = false,
    val theme: UserSettingsDto.Theme = UserSettingsDto.Theme.LIGHT
) {
    fun toDto(): UserSettingsDto {
        return UserSettingsDto(
            email = email,
            areNotificationsEnabled = areNotificationsEnabled,
            theme = theme
        )
    }

    companion object {
        fun fromDto(dto: UserSettingsDto): UserSettingsState {
            return UserSettingsState(
                email = dto.email,
                areNotificationsEnabled = dto.areNotificationsEnabled,
                theme = dto.theme
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserSettingsState

        if (areNotificationsEnabled != other.areNotificationsEnabled) return false
        if (email != other.email) return false
        if (theme != other.theme) return false

        return true
    }

    override fun hashCode(): Int {
        var result = areNotificationsEnabled.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + theme.hashCode()
        return result
    }
}
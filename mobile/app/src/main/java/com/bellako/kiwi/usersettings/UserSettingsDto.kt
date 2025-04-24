package com.bellako.kiwi.usersettings

data class UserSettingsDto(
    val id: Int = 1, // TODO: Remove when JWT is implemented
    val email: String = "",
    val areNotificationsEnabled: Boolean = false,
    val theme: Theme = Theme.LIGHT
) {
    enum class Theme {
        LIGHT,
        DARK
    }

    fun toState() = UserSettingsState.fromDto(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserSettingsDto

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


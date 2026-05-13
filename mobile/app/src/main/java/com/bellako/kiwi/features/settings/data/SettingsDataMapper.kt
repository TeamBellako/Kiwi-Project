package com.bellako.kiwi.features.settings.data

import android.os.Build
import androidx.annotation.RequiresApi

object SettingsDataMapper {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(dto: SettingsDTO): SettingsDomain =
        SettingsDomain(
            soundVolume = dto.soundVolume,
            musicVolume = dto.musicVolume,
        )

    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(state: SettingsState): SettingsDomain =
        SettingsDomain(
            soundVolume = state.soundVolume,
            musicVolume = state.musicVolume,
        )

    fun toState(domain: SettingsDomain): SettingsState =
        SettingsState(
            soundVolume = domain.soundVolume,
            musicVolume = domain.musicVolume,
        )

    @RequiresApi(Build.VERSION_CODES.O)
    fun toState(dto: SettingsDTO): SettingsState = toState(toDomain(dto))

    fun toDTO(domain: SettingsDomain): SettingsDTO =
        SettingsDTO(
            soundVolume = domain.soundVolume,
            musicVolume = domain.musicVolume,
        )

    @RequiresApi(Build.VERSION_CODES.O)
    fun toDTO(state: SettingsState): SettingsDTO = toDTO(toDomain(state))
}

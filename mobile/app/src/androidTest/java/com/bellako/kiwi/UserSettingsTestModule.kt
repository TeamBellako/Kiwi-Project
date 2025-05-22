package com.bellako.kiwi

import com.bellako.kiwi.userSettings.network.IUserSettingsAPI
import com.bellako.kiwi.userSettings.network.UserSettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.mockito.Mockito

@Module
@InstallIn(SingletonComponent::class)
object UserSettingsTestModule {
    @Provides
    fun provideMockUserSettingsApi(): IUserSettingsAPI {
        return Mockito.mock(IUserSettingsAPI::class.java)
    }

    @Provides
    fun provideMockUserSettingsRepository(
        api: IUserSettingsAPI
    ): UserSettingsRepository {
        return UserSettingsRepository(api)
    }
}
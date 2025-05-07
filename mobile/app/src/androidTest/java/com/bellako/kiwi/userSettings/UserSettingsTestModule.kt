package com.bellako.kiwi.userSettings

import com.bellako.kiwi.userSettings.network.IUserSettingsAPI
import com.bellako.kiwi.userSettings.network.UserSettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.mockito.Mockito.mock

@Module
@InstallIn(SingletonComponent::class)
object UserSettingsTestModule {
    @Provides
    fun provideMockUserSettingsApi(): IUserSettingsAPI {
        return mock(IUserSettingsAPI::class.java)
    }

    @Provides
    fun provideMockUserSettingsRepository(
        api: IUserSettingsAPI
    ): UserSettingsRepository {
        return UserSettingsRepository(api)
    }
}

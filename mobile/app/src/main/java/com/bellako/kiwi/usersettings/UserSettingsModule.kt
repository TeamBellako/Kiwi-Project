package com.bellako.kiwi.usersettings

import com.bellako.kiwi.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object UserSettingsModule {
    @Provides
    fun provideUserSettingsApi(): IUserSettingsApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.MOBILE_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IUserSettingsApi::class.java)
    }

    @Provides
    fun provideUserSettingsRepository(
        api: IUserSettingsApi
    ): UserSettingsRepository {
        return UserSettingsRepository(api)
    }
}

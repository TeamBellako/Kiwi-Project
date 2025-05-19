package com.bellako.kiwi.userSettings.network

import com.bellako.kiwi.BuildConfig
import com.bellako.kiwi.network.AuthRepository
import com.bellako.kiwi.network.JwtAuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object UserSettingsModule {

    @Provides
    fun provideJwtAuthInterceptor(authRepository: AuthRepository): JwtAuthInterceptor {
        return JwtAuthInterceptor(authRepository)
    }

    @Provides
    fun provideUserSettingsApi(
        jwtAuthInterceptor: JwtAuthInterceptor
    ): IUserSettingsAPI {
        val client = OkHttpClient.Builder()
            .addInterceptor(jwtAuthInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.MOBILE_API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IUserSettingsAPI::class.java)
    }

    @Provides
    fun provideUserSettingsRepository(
        api: IUserSettingsAPI
    ): UserSettingsRepository {
        return UserSettingsRepository(api)
    }
}
package com.bellako.kiwi.userSettings

import com.bellako.kiwi.BuildConfig
import com.bellako.kiwi.network.AuthRepository
import com.bellako.kiwi.network.HealthApiService
import com.bellako.kiwi.network.JwtAuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserSettingsModule {

    @Provides
    @Singleton
    fun provideJwtAuthInterceptor(authRepository: AuthRepository): JwtAuthInterceptor {
        return JwtAuthInterceptor(authRepository)
    }

    @Provides
    @Singleton
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
    @Singleton
    fun provideHealthApiService(): HealthApiService {
        val shortTimeoutClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.MOBILE_API_URL)
            .client(shortTimeoutClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HealthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserSettingsRepository(
        api: IUserSettingsAPI,
        healthApiService: HealthApiService
    ): UserSettingsRepository {
        return UserSettingsRepository(api, healthApiService)
    }
}
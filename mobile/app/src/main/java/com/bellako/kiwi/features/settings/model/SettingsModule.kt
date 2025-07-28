package com.bellako.kiwi.features.settings.model

import com.bellako.kiwi.BuildConfig
import com.bellako.kiwi.features.users.model.AuthRepository
import com.bellako.kiwi.common.model.HealthApiService
import com.bellako.kiwi.features.users.model.JwtAuthInterceptor
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
object SettingsModule {
    @Provides
    @Singleton
    fun provideJwtAuthInterceptor(authRepository: AuthRepository): JwtAuthInterceptor {
        return JwtAuthInterceptor(authRepository)
    }

    @Provides
    @Singleton
    fun provideSettingsApi(
        jwtAuthInterceptor: JwtAuthInterceptor
    ): ISettingsAPI {
        val client = OkHttpClient.Builder()
            .addInterceptor(jwtAuthInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.MOBILE_API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ISettingsAPI::class.java)
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
    fun provideSettingsRepository(
        api: ISettingsAPI,
        healthApiService: HealthApiService
    ): SettingsRepository {
        return SettingsRepository(api, healthApiService)
    }
}
package com.bellako.kiwi.features.tips.model

import com.bellako.kiwi.BuildConfig
import com.bellako.kiwi.features.users.model.JwtAuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object TipsModule {
    @Provides
    fun provideAPI(jwtAuthInterceptor: JwtAuthInterceptor): ITipsAPI {
        val client =
            okhttp3.OkHttpClient
                .Builder()
                .addInterceptor(jwtAuthInterceptor)
                .build()

        return Retrofit
            .Builder()
            .baseUrl(BuildConfig.MOBILE_API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITipsAPI::class.java)
    }

    @Provides
    fun provideTipsRepository(api: ITipsAPI): TipsRepository = TipsRepository(api)
}

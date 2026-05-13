package com.bellako.kiwi.features.combat.model

import com.bellako.kiwi.BuildConfig
import com.bellako.kiwi.features.users.model.JwtAuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CombatModule {
    @Provides
    @Singleton
    fun provideCombatApi(jwtAuthInterceptor: JwtAuthInterceptor): ICombatAPI =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.MOBILE_API_URL)
            .client(OkHttpClient.Builder().addInterceptor(jwtAuthInterceptor).build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ICombatAPI::class.java)

    @Provides
    @Singleton
    fun provideCombatRepository(api: ICombatAPI): CombatRepository = CombatRepository(api)
}

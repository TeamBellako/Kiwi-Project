package com.bellako.kiwi.features.goals.model

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
object GoalsModule {
    @Provides
    @Singleton
    fun provideGoalsApi(jwtAuthInterceptor: JwtAuthInterceptor): IGoalsAPI {
        val client =
            OkHttpClient
                .Builder()
                .addInterceptor(jwtAuthInterceptor)
                .build()

        return Retrofit
            .Builder()
            .baseUrl(BuildConfig.MOBILE_API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IGoalsAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideGoalsRepository(api: IGoalsAPI): GoalsRepository = GoalsRepository(api)
}

package com.bellako.kiwi.features.users.model

import com.bellako.kiwi.BuildConfig
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
object UsersModule {
    @Provides
    @Singleton
    fun provideUsersApi(jwtAuthInterceptor: JwtAuthInterceptor): IUsersAPI {
        val client = OkHttpClient
            .Builder()
            .addInterceptor(jwtAuthInterceptor)
            .build()

        return Retrofit
            .Builder()
            .baseUrl(BuildConfig.MOBILE_API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IUsersAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideUsersRepository(api: IUsersAPI): UsersRepository = UsersRepository(api)
}

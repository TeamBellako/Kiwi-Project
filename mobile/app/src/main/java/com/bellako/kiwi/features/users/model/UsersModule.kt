package com.bellako.kiwi.features.users.model

import com.bellako.kiwi.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object UsersModule {
    @Provides
    fun provideUsersApi(): IUsersAPI {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.MOBILE_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IUsersAPI::class.java)
    }

    @Provides
    fun provideUsersRepository(
        api: IUsersAPI
    ): UsersRepository {
        return UsersRepository(api)
    }
}
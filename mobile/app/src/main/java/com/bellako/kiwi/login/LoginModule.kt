package com.bellako.kiwi.login

import com.bellako.kiwi.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {
    @Provides
    fun provideLoginApi(): ILoginAPI {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.MOBILE_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ILoginAPI::class.java)
    }

    @Provides
    fun provideLoginRepository(
        api: ILoginAPI
    ): LoginRepository {
        return LoginRepository(api)
    }
}
package com.bellako.kiwi.features.conversations.model

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
object ConversationsModule {
    @Provides
    @Singleton
    fun provideConversationsApi(jwtAuthInterceptor: JwtAuthInterceptor): IConversationsAPI =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.MOBILE_API_URL)
            .client(OkHttpClient.Builder().addInterceptor(jwtAuthInterceptor).build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IConversationsAPI::class.java)

    @Provides
    @Singleton
    fun provideConversationsRepository(api: IConversationsAPI): ConversationsRepository = ConversationsRepository(api)
}

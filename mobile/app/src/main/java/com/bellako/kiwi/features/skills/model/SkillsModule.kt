package com.bellako.kiwi.features.skills.model

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
object SkillsModule {
    @Provides
    fun provideQuestsApi(jwtAuthInterceptor: JwtAuthInterceptor): ISkillsAPI {
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
            .create(ISkillsAPI::class.java)
    }

    @Provides
    fun provideSkillsRepository(api: ISkillsAPI): SkillsRepository = SkillsRepository(api)
}

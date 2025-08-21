package com.bellako.kiwi.features.metrics.model

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
object MetricsModule {
    @Provides
    @Singleton
    fun provideMetricsApi(jwtAuthInterceptor: JwtAuthInterceptor): IMetricsAPI {
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
            .create(IMetricsAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideMetricsRepository(api: IMetricsAPI): MetricsRepository = MetricsRepository(api)
}

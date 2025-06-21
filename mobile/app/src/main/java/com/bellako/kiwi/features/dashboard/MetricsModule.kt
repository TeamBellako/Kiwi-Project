package com.bellako.kiwi.features.dashboard

import com.bellako.kiwi.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object MetricsModule {
    @Provides
    fun provideMetricsApi(): IMetricsAPI {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.MOBILE_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IMetricsAPI::class.java)
    }

    @Provides
    fun provideMetricsRepository(
        api: IMetricsAPI
    ): MetricsRepository {
        return MetricsRepository(api)
    }
}
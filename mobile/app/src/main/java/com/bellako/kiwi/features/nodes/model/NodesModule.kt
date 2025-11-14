package com.bellako.kiwi.features.nodes.model

import com.bellako.kiwi.BuildConfig
import com.bellako.kiwi.features.users.model.JwtAuthInterceptor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NodesModule {
    @Provides
    fun provideNodesApi(jwtAuthInterceptor: JwtAuthInterceptor): INodesAPI {
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
            .create(INodesAPI::class.java)
    }

    @Provides
    fun provideNodesRepository(api: INodesAPI): NodesRepository = NodesRepository(api)
}

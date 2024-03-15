package com.harian.closer.share.location.data.common.module

import com.harian.software.closer.share.location.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
object StompModule {

    @Provides
    @Singleton
    fun provideStompClient(okHttpClient: OkHttpClient): StompClient {
        return Stomp.over(Stomp.ConnectionProvider.OKHTTP, BuildConfig.WEB_SOCKET_END_POINT, null, okHttpClient)
    }
}

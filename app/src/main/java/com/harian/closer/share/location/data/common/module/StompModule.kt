package com.harian.closer.share.location.data.common.module

import android.annotation.SuppressLint
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.config.HeartBeat
import org.hildan.krossbow.stomp.config.StompConfig
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
object StompModule {

    @SuppressLint("CheckResult")
    @Provides
    @Singleton
    fun provideStompClient(okHttpClient: OkHttpClient): StompClient {
        val stompConfig = StompConfig().apply {
            connectionTimeout = 3.seconds
            gracefulDisconnect = false
        }
        return StompClient(OkHttpWebSocketClient(okHttpClient), stompConfig)
    }
}

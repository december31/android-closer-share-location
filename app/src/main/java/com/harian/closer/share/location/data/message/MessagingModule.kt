package com.harian.closer.share.location.data.message

import com.harian.closer.share.location.data.common.module.NetworkModule
import com.harian.closer.share.location.data.common.module.SharedPrefModule
import com.harian.closer.share.location.data.common.module.StompModule
import com.harian.closer.share.location.data.message.remote.api.MessageApi
import com.harian.closer.share.location.data.message.repository.MessageRepositoryImpl
import com.harian.closer.share.location.domain.message.MessageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import ua.naiksoftware.stomp.StompClient
import javax.inject.Singleton

@Module(includes = [StompModule::class, SharedPrefModule::class, NetworkModule::class])
@InstallIn(SingletonComponent::class)
object MessagingModule {

    @Provides
    @Singleton
    fun provideMessagingRepository(messageApi: MessageApi, stompClient: StompClient): MessageRepository {
        return MessageRepositoryImpl(messageApi, stompClient)
    }

    @Provides
    @Singleton
    fun provideMessageApi(retrofit: Retrofit): MessageApi {
        return retrofit.create(MessageApi::class.java)
    }
}

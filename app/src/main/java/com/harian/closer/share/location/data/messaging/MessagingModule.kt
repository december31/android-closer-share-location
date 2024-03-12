package com.harian.closer.share.location.data.messaging

import com.harian.closer.share.location.data.common.module.SharedPrefModule
import com.harian.closer.share.location.data.common.module.StompModule
import com.harian.closer.share.location.data.messaging.repository.MessagingRepositoryImpl
import com.harian.closer.share.location.domain.messaging.MessagingRepository
import com.harian.closer.share.location.platform.SharedPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.hildan.krossbow.stomp.StompClient
import javax.inject.Singleton

@Module(includes = [StompModule::class, SharedPrefModule::class])
@InstallIn(SingletonComponent::class)
object MessagingModule {

    @Provides
    @Singleton
    fun provideMessagingRepository(stompClient: StompClient, sharedPrefs: SharedPrefs): MessagingRepository {
        return MessagingRepositoryImpl(stompClient, sharedPrefs)
    }
}

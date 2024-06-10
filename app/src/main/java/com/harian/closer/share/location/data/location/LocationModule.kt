package com.harian.closer.share.location.data.location

import com.harian.closer.share.location.data.common.module.SharedPrefModule
import com.harian.closer.share.location.data.common.module.StompModule
import com.harian.closer.share.location.data.location.repository.LocationRepositoryImpl
import com.harian.closer.share.location.domain.location.LocationRepository
import com.harian.closer.share.location.platform.SharedPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.naiksoftware.stomp.StompClient
import javax.inject.Singleton

@Module(includes = [StompModule::class, SharedPrefModule::class])
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    @Singleton
    fun provideLocationRepository(stompClient: StompClient, sharedPrefs: SharedPrefs) : LocationRepository{
        return LocationRepositoryImpl(stompClient, sharedPrefs)
    }
}

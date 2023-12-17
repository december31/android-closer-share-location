package com.harian.closer.share.location.data.refresh.token

import com.harian.closer.share.location.data.common.module.NetworkModule
import com.harian.closer.share.location.data.common.module.SharedPrefModule
import com.harian.closer.share.location.data.refresh.token.remote.api.RefreshTokenApi
import com.harian.closer.share.location.data.refresh.token.repository.RefreshTokenRepositoryImpl
import com.harian.closer.share.location.domain.refresh.token.RefreshTokenRepository
import com.harian.closer.share.location.platform.SharedPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class, SharedPrefModule::class])
@InstallIn(SingletonComponent::class)
object RefreshTokenModule {

    @Provides
    @Singleton
    fun provideRefreshTokenApi(retrofit: Retrofit): RefreshTokenApi {
        return retrofit.create(RefreshTokenApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRefreshTokenRepository(refreshTokenApi: RefreshTokenApi, sharedPrefs: SharedPrefs): RefreshTokenRepository {
        return RefreshTokenRepositoryImpl(refreshTokenApi, sharedPrefs)
    }
}

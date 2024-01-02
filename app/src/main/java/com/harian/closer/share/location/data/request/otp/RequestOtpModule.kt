package com.harian.closer.share.location.data.request.otp

import com.harian.closer.share.location.data.common.module.NetworkModule
import com.harian.closer.share.location.data.request.otp.remote.api.RequestOtpApi
import com.harian.closer.share.location.data.request.otp.repository.RequestOtpRepositoryImpl
import com.harian.closer.share.location.domain.request.otp.RequestOtpRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
object RequestOtpModule {
    @Provides
    @Singleton
    fun provideRequestOtpApi(retrofit: Retrofit): RequestOtpApi {
        return retrofit.create(RequestOtpApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRequestOtpRepository(requestOtpApi: RequestOtpApi): RequestOtpRepository {
        return RequestOtpRepositoryImpl(requestOtpApi)
    }
}

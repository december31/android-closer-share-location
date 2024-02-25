package com.harian.closer.share.location.data.resetpassword

import com.harian.closer.share.location.data.common.module.NetworkModule
import com.harian.closer.share.location.data.resetpassword.remote.api.ResetPasswordApi
import com.harian.closer.share.location.data.resetpassword.repository.ResetPasswordRepositoryImpl
import com.harian.closer.share.location.domain.resetpassword.ResetPasswordRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
object ResetPasswordModule {

    @Provides
    @Singleton
    fun provideResetPasswordApi(retrofit: Retrofit): ResetPasswordApi {
        return retrofit.create(ResetPasswordApi::class.java)
    }

    @Provides
    @Singleton
    fun provideResetPasswordRepository(resetPasswordApi: ResetPasswordApi): ResetPasswordRepository {
        return ResetPasswordRepositoryImpl(resetPasswordApi)
    }
}

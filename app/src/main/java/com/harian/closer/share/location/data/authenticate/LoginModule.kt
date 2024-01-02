package com.harian.closer.share.location.data.authenticate

import com.harian.closer.share.location.data.common.module.NetworkModule
import com.harian.closer.share.location.data.authenticate.remote.api.AuthenticateApi
import com.harian.closer.share.location.data.authenticate.repository.AuthenticateRepositoryImpl
import com.harian.closer.share.location.domain.login.AuthenticateRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
object LoginModule {

    @Provides
    @Singleton
    fun provideAuthenticateApi(retrofit: Retrofit): AuthenticateApi {
        return retrofit.create(AuthenticateApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthenticateRepository(authenticateApi: AuthenticateApi): AuthenticateRepository {
        return AuthenticateRepositoryImpl(authenticateApi)
    }
}

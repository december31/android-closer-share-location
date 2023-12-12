package com.harian.closer.share.location.data.register

import com.harian.closer.share.location.data.common.module.NetworkModule
import com.harian.closer.share.location.data.register.remote.api.RegisterApi
import com.harian.closer.share.location.data.register.repository.RegisterRepositoryImpl
import com.harian.closer.share.location.domain.register.RegisterRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class RegisterModule {
    @Singleton
    @Provides
    fun provideRegisterApi(retrofit: Retrofit) : RegisterApi {
        return retrofit.create(RegisterApi::class.java)
    }

    @Singleton
    @Provides
    fun provideRegisterRepository(registerApi: RegisterApi) : RegisterRepository {
        return RegisterRepositoryImpl(registerApi)
    }
}

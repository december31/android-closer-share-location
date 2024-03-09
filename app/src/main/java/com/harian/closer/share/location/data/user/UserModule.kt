package com.harian.closer.share.location.data.user

import com.harian.closer.share.location.data.common.module.NetworkModule
import com.harian.closer.share.location.data.user.remote.api.UserApi
import com.harian.closer.share.location.data.user.repository.UserRepositoryImpl
import com.harian.closer.share.location.domain.user.UserRepository
import com.harian.closer.share.location.utils.ResponseUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
object UserModule {
    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserRepository(userApi: UserApi, responseUtil: ResponseUtil): UserRepository {
        return UserRepositoryImpl(userApi, responseUtil)
    }
}

package com.harian.closer.share.location.data.post

import com.harian.closer.share.location.data.common.module.NetworkModule
import com.harian.closer.share.location.data.post.remote.api.PostApi
import com.harian.closer.share.location.data.post.repository.PostRepositoryImpl
import com.harian.closer.share.location.domain.post.PostRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
object PostModule {
    @Provides
    @Singleton
    fun providePostApi(retrofit: Retrofit): PostApi {
        return retrofit.create(PostApi::class.java)
    }

    @Provides
    @Singleton
    fun providePostRepository(postApi: PostApi): PostRepository {
        return PostRepositoryImpl(postApi)
    }
}

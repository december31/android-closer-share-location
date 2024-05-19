package com.harian.closer.share.location.data.search

import com.harian.closer.share.location.data.common.module.NetworkModule
import com.harian.closer.share.location.data.search.remote.api.SearchApi
import com.harian.closer.share.location.data.search.repository.SearchRepositoryImpl
import com.harian.closer.share.location.domain.search.SearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
object SearchModule {

    @Provides
    @Singleton
    fun provideSearchApi(retrofit: Retrofit): SearchApi {
        return retrofit.create(SearchApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSearchRepository(searchApi: SearchApi): SearchRepository {
        return SearchRepositoryImpl(searchApi)
    }
}

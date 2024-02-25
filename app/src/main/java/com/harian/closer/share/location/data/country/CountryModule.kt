package com.harian.closer.share.location.data.country

import com.harian.closer.share.location.data.common.module.NetworkModule
import com.harian.closer.share.location.data.country.remote.api.CountryApi
import com.harian.closer.share.location.data.country.repository.CountryRepositoryImpl
import com.harian.closer.share.location.domain.country.CountryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
object CountryModule {
    @Provides
    @Singleton
    fun provideCountryApi(retrofit: Retrofit): CountryApi {
        return retrofit.create(CountryApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCountryRepository(countryApi: CountryApi): CountryRepository {
        return CountryRepositoryImpl(countryApi)
    }
}

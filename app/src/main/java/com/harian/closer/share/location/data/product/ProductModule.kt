package com.harian.closer.share.location.data.product

import com.harian.closer.share.location.data.common.module.NetworkModule
import com.harian.closer.share.location.data.product.remote.api.ProductApi
import com.harian.closer.share.location.data.product.repository.ProductRepositoryImpl
import com.harian.closer.share.location.domain.product.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class ProductModule {
    @Singleton
    @Provides
    fun provideProductApi(retrofit: Retrofit) : ProductApi {
        return retrofit.create(ProductApi::class.java)
    }

    @Singleton
    @Provides
    fun provideProductRepository(productApi: ProductApi) : ProductRepository {
        return ProductRepositoryImpl(productApi)
    }
}

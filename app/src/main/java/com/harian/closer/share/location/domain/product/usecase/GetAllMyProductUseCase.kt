package com.harian.closer.share.location.domain.product.usecase

import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.product.remote.dto.ProductResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.product.ProductRepository
import com.harian.closer.share.location.domain.product.entity.ProductEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllMyProductUseCase @Inject constructor(private val productRepository: ProductRepository) {
    suspend fun invoke() : Flow<BaseResult<List<ProductEntity>, WrappedListResponse<ProductResponse>>> {
        return productRepository.getAllMyProducts()
    }
}

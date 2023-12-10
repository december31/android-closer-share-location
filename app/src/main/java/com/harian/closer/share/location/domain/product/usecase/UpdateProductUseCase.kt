package com.harian.closer.share.location.domain.product.usecase

import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.product.remote.dto.ProductResponse
import com.harian.closer.share.location.data.product.remote.dto.ProductUpdateRequest
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.product.ProductRepository
import com.harian.closer.share.location.domain.product.entity.ProductEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateProductUseCase @Inject constructor(private val productRepository: ProductRepository){
    suspend fun invoke(productUpdateRequest: ProductUpdateRequest, id: String) : Flow<BaseResult<ProductEntity, WrappedResponse<ProductResponse>>> {
        return productRepository.updateProduct(productUpdateRequest, id)
    }
}

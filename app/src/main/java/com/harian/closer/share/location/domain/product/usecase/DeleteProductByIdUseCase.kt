package com.harian.closer.share.location.domain.product.usecase

import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.product.remote.dto.ProductResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.product.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteProductByIdUseCase @Inject constructor(private val productRepository: ProductRepository) {
    suspend fun invoke(id: String) : Flow<BaseResult<Unit, WrappedResponse<ProductResponse>>> {
        return productRepository.deleteProductById(id)
    }
}

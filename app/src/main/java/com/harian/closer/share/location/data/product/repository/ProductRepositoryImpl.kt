package com.harian.closer.share.location.data.product.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.product.remote.api.ProductApi
import com.harian.closer.share.location.data.product.remote.dto.ProductCreateRequest
import com.harian.closer.share.location.data.product.remote.dto.ProductResponse
import com.harian.closer.share.location.data.product.remote.dto.ProductUpdateRequest
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.product.ProductRepository
import com.harian.closer.share.location.domain.product.entity.ProductEntity
import com.harian.closer.share.location.domain.product.entity.ProductUserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(private val productApi: ProductApi) :
    ProductRepository {
    override suspend fun getAllMyProducts(): Flow<BaseResult<List<ProductEntity>, WrappedListResponse<ProductResponse>>> {
        return flow {
            val response = productApi.getAllMyProducts()
            if (response.isSuccessful){
                val body = response.body()!!
                val products = mutableListOf<ProductEntity>()
                var user: ProductUserEntity?
                body.data?.forEach { productResponse ->
                    user = ProductUserEntity(productResponse.user.id, productResponse.user.name, productResponse.user.email)
                    products.add(
                        ProductEntity(
                        productResponse.id,
                        productResponse.name,
                        productResponse.price,
                        user!!
                    )
                    )
                }
                emit(BaseResult.Success(products))
            }else{
                val type = object : TypeToken<WrappedListResponse<ProductResponse>>(){}.type
                val err = Gson().fromJson<WrappedListResponse<ProductResponse>>(response.errorBody()!!.charStream(), type)!!
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }

    override suspend fun getProductById(id: String): Flow<BaseResult<ProductEntity, WrappedResponse<ProductResponse>>> {
        return flow {
            val response = productApi.getProductById(id)
            if(response.isSuccessful){
                val body = response.body()!!
                val user = ProductUserEntity(body.data?.user?.id!!, body.data?.user?.name!!, body.data?.user?.email!!)
                val product = ProductEntity(body.data?.id!!, body.data?.name!!, body.data?.price!!, user)
                emit(BaseResult.Success(product))
            }else{
                val type = object : TypeToken<WrappedResponse<ProductResponse>>(){}.type
                val err = Gson().fromJson<WrappedResponse<ProductResponse>>(response.errorBody()!!.charStream(), type)!!
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }

    override suspend fun updateProduct(productUpdateRequest: ProductUpdateRequest, id: String): Flow<BaseResult<ProductEntity, WrappedResponse<ProductResponse>>> {
        return flow {
            val response = productApi.updateProduct(productUpdateRequest, id)
            if(response.isSuccessful){
                val body = response.body()!!
                val user = ProductUserEntity(body.data?.user?.id!!, body.data?.user?.name!!, body.data?.user?.email!!)
                val product = ProductEntity(body.data?.id!!, body.data?.name!!, body.data?.price!!, user)
                emit(BaseResult.Success(product))
            }else{
                val type = object : TypeToken<WrappedResponse<ProductResponse>>(){}.type
                val err = Gson().fromJson<WrappedResponse<ProductResponse>>(response.errorBody()!!.charStream(), type)!!
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }

    override suspend fun deleteProductById(id: String): Flow<BaseResult<Unit, WrappedResponse<ProductResponse>>> {
        return flow{
            val response = productApi.deleteProduct(id)
            if(response.isSuccessful){
                emit(BaseResult.Success(Unit))
            }else{
                val type = object : TypeToken<WrappedResponse<ProductResponse>>(){}.type
                val err = Gson().fromJson<WrappedResponse<ProductResponse>>(response.errorBody()!!.charStream(), type)!!
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }

    override suspend fun createProduct(productCreateRequest: ProductCreateRequest): Flow<BaseResult<ProductEntity, WrappedResponse<ProductResponse>>> {
        return flow {
            val response = productApi.createProduct(productCreateRequest)
            if(response.isSuccessful){
                val body = response.body()!!
                val user = ProductUserEntity(body.data?.user?.id!!, body.data?.user?.name!!, body.data?.user?.email!!)
                val product = ProductEntity(body.data?.id!!, body.data?.name!!, body.data?.price!!, user)
                emit(BaseResult.Success(product))
            }else{
                val type = object : TypeToken<WrappedResponse<ProductResponse>>(){}.type
                val err = Gson().fromJson<WrappedResponse<ProductResponse>>(response.errorBody()!!.charStream(), type)!!
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }


}

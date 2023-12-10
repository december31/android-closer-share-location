package com.harian.closer.share.location.data.product.remote.api

import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.product.remote.dto.ProductCreateRequest
import com.harian.closer.share.location.data.product.remote.dto.ProductResponse
import com.harian.closer.share.location.data.product.remote.dto.ProductUpdateRequest
import retrofit2.Response
import retrofit2.http.*

interface ProductApi {
    @GET("product/")
    suspend fun getAllMyProducts() : Response<WrappedListResponse<ProductResponse>>

    @GET("product/{id}")
    suspend fun getProductById(@Path("id") id: String) : Response<WrappedResponse<ProductResponse>>

    @PUT("product/{id}")
    suspend fun updateProduct(@Body productUpdateRequest: ProductUpdateRequest, @Path("id") id: String) : Response<WrappedResponse<ProductResponse>>

    @DELETE("product/{id}")
    suspend fun deleteProduct(@Path("id") id: String) : Response<WrappedResponse<ProductResponse>>

    @POST("product/")
    suspend fun createProduct(@Body productCreateRequest: ProductCreateRequest) : Response<WrappedResponse<ProductResponse>>
}

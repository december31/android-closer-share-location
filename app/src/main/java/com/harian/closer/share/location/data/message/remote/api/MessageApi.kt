package com.harian.closer.share.location.data.message.remote.api

import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.message.remote.dto.MessageDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MessageApi {
    @POST("api/v1/message/send")
    suspend fun sendMessage(@Body messageDTO: MessageDTO): Response<WrappedResponse<MessageDTO>>

    @GET("api/v1/message/{id}")
    suspend fun getMessages(@Path("id") userId: Long): Response<WrappedListResponse<MessageDTO>>
}

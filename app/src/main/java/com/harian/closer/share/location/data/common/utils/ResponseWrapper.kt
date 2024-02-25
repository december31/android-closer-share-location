package com.harian.closer.share.location.data.common.utils

import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

sealed class BaseResponse<T : Any>(): Type {
    data class Success<T : Any>(
        @SerializedName("status") val status: Int,
        @SerializedName("message") val message: String?,
        @SerializedName("error") val errors: String? = null,
        @SerializedName("data") val data: T? = null,
    ) : BaseResponse<T>()

    data class Error(
        @SerializedName("status") val status: Int,
        @SerializedName("message") val message: String?,
        @SerializedName("error") val error: String? = null,
    ) : BaseResponse<Unit>()

    data class UnknownError(val exception: Throwable) : BaseResponse<Throwable>()
}

data class WrappedResponse<T>(
    var code: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("errors") val errors: List<String>? = null,
    @SerializedName("data") val data: T? = null,
)

data class WrappedListResponse<T>(
    var code: Int,
    @SerializedName("message") var message: String,
    @SerializedName("status") var status: Int,
    @SerializedName("errors") var errors: List<String>? = null,
    @SerializedName("data") var data: List<T>? = null
)

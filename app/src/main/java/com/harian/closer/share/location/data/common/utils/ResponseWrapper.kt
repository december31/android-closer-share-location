package com.harian.closer.share.location.data.common.utils

import com.google.gson.annotations.SerializedName

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

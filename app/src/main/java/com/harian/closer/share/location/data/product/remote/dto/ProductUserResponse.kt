package com.harian.closer.share.location.data.product.remote.dto

import com.google.gson.annotations.SerializedName

data class ProductUserResponse(
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: String,
    @SerializedName("email") var email: String
)

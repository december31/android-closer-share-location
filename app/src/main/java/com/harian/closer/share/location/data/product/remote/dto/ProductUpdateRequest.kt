package com.harian.closer.share.location.data.product.remote.dto

import com.google.gson.annotations.SerializedName

data class ProductUpdateRequest(
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Int
)

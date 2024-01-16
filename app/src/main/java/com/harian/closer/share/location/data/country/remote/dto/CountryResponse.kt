package com.harian.closer.share.location.data.country.remote.dto

import com.google.gson.annotations.SerializedName

data class CountryResponse (
    @SerializedName("code") val code: String?,
    @SerializedName("name") val name: String
)

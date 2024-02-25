package com.harian.closer.share.location.data.authenticate.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthenticateRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
)

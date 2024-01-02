package com.harian.closer.share.location.data.authenticate.remote.dto

import com.google.gson.annotations.SerializedName

data class OtpAuthenticateRequest(
    @SerializedName("email") val email: String,
    @SerializedName("otp") val password: String,
)

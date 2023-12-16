package com.harian.closer.share.location.data.request.otp.remote.dto

import com.google.gson.annotations.SerializedName

data class RequestOtpRequest(
    @SerializedName("email") val email: String?
)

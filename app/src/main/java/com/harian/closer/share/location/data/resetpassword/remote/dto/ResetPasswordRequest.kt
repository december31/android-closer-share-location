package com.harian.closer.share.location.data.resetpassword.remote.dto

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequest(
    @SerializedName("new_password") val newPassword: String
)

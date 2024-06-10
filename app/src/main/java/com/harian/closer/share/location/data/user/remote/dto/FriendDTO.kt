package com.harian.closer.share.location.data.user.remote.dto

import com.google.gson.annotations.SerializedName

data class FriendDTO(
    @SerializedName("information") val information: UserDTO,
    @SerializedName("since") val since: Long
)

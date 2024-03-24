package com.harian.closer.share.location.data.post.remote.dto

import com.google.gson.annotations.SerializedName
import com.harian.closer.share.location.data.user.remote.dto.UserDTO


data class ImageResponse(
    @SerializedName("id") val id: Int?,
    @SerializedName("url") val url: String?,
    @SerializedName("comments") val comments: List<UserDTO>?,
    @SerializedName("likes") val likes: List<UserDTO>?,
)


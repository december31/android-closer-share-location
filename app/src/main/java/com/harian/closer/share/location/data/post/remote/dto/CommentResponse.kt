package com.harian.closer.share.location.data.post.remote.dto

import com.google.gson.annotations.SerializedName
import com.harian.closer.share.location.data.user.remote.dto.UserResponse

data class CommentResponse (
    @SerializedName("id") val id: Int?,
    @SerializedName("createdTime") val createdTime: Long?,
    @SerializedName("owner") val owner: UserResponse?,
    @SerializedName("content") val content: String?,
)

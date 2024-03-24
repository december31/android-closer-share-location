package com.harian.closer.share.location.data.post.remote.dto

import com.google.gson.annotations.SerializedName
import com.harian.closer.share.location.data.user.remote.dto.UserDTO

data class PostDTO(
    @SerializedName("id") val id: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("content") val content: String?,
    @SerializedName("images") val images: List<ImageResponse>?,
    @SerializedName("createdTime") val createdTime: Long,
    @SerializedName("lastModified") val lastModified: Long,
    @SerializedName("owner") val owner: UserDTO?,
    @SerializedName("comments") val comments: List<CommentResponse?>?,
    @SerializedName("likes") val likes: List<UserDTO?>?,
)

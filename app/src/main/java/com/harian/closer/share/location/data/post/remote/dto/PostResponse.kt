package com.harian.closer.share.location.data.post.remote.dto

import com.google.gson.annotations.SerializedName
import com.harian.closer.share.location.data.comment.remote.dto.CommentResponse
import com.harian.closer.share.location.data.user.remote.dto.UserResponse

data class PostResponse(
    @SerializedName("id") val id: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("content") val content: String?,
    @SerializedName("images") val imageUrls: List<String?>?,
    @SerializedName("createdTime") val createdTime: Long,
    @SerializedName("lastModified") val lastModified: Long,
    @SerializedName("owner") val owner: UserResponse?,
    @SerializedName("comments") val comments: List<CommentResponse?>?,
    @SerializedName("likes") val likes: List<UserResponse?>?,
)

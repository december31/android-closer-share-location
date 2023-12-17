package com.harian.closer.share.location.data.post.remote.dto

import com.google.gson.annotations.SerializedName

data class CreatePostRequest (
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
)

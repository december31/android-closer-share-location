package com.harian.closer.share.location.data.post.remote.dto

import com.google.gson.annotations.SerializedName

data class CommentRequest (
    @SerializedName("content") val content: String?
)

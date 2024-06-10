package com.harian.closer.share.location.data.post.remote.dto

import com.google.gson.annotations.SerializedName
import com.harian.closer.share.location.data.common.base.BaseDTO
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import com.harian.closer.share.location.domain.post.entity.ImageEntity


data class ImageResponse(
    @SerializedName("id") val id: Int?,
    @SerializedName("url") val url: String?,
    @SerializedName("comments") val comments: List<UserDTO>?,
    @SerializedName("likes") val likes: List<UserDTO>?,
) : BaseDTO<ImageEntity> {
    override fun toEntity(): ImageEntity {
        return ImageEntity(
            id = id,
            url = url,
            comments = comments?.map { it.toEntity() },
            likes = likes?.map { it.toEntity() }
        )
    }
}


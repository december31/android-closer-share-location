package com.harian.closer.share.location.data.post.remote.dto

import com.google.gson.annotations.SerializedName
import com.harian.closer.share.location.data.common.base.BaseDTO
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import com.harian.closer.share.location.domain.post.entity.PostEntity

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
): BaseDTO<PostEntity> {
    override fun toEntity(): PostEntity {
        return PostEntity(
            id = id,
            title = title,
            content = content,
            images = images?.map { it.toEntity() },
            createdTime = createdTime,
            lastModified = lastModified,
            owner = owner?.toEntity(),
            comments = comments?.map { it?.toEntity() },
            likes = likes?.map { it?.toEntity() }
        )
    }
}

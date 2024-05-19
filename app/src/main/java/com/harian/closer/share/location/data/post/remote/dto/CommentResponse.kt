package com.harian.closer.share.location.data.post.remote.dto

import com.google.gson.annotations.SerializedName
import com.harian.closer.share.location.data.common.base.BaseDTO
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import com.harian.closer.share.location.domain.comment.entity.CommentEntity

data class CommentResponse(
    @SerializedName("id") val id: Int?,
    @SerializedName("createdTime") val createdTime: Long?,
    @SerializedName("owner") val owner: UserDTO?,
    @SerializedName("content") val content: String?,
) : BaseDTO<CommentEntity> {
    override fun toEntity(): CommentEntity {
        return CommentEntity(
            id = id,
            createdTime = createdTime,
            owner = owner?.toEntity(),
            content = content
        )
    }
}

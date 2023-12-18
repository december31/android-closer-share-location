package com.harian.closer.share.location.domain.comment.entity

import com.harian.closer.share.location.domain.user.entity.UserEntity

data class CommentEntity (
    val id: Int?,
    val content: String?,
    val createdTime: Long?,
    val owner: UserEntity?,
)

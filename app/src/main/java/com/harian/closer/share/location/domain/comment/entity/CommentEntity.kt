package com.harian.closer.share.location.domain.comment.entity

import com.harian.closer.share.location.domain.user.entity.UserEntity

data class CommentEntity(
    val id: Int?,
    val content: String?,
    val createdTime: Long?,
    val owner: UserEntity?,
) : Comparable<CommentEntity> {
    override fun compareTo(other: CommentEntity): Int {
        return if (this.id == other.id &&
            this.content == other.content &&
            this.createdTime == other.createdTime
        ) 0 else 1
    }
}

package com.harian.closer.share.location.domain.post.entity

import com.harian.closer.share.location.domain.comment.entity.CommentEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity

data class PostEntity(
    val id: Int?,
    val title: String?,
    val content: String?,
    val imageUrls: List<String?>?,
    val createdTime: Long?,
    val lastModified: Long?,
    val owner: UserEntity?,
    val likes: List<UserEntity?>?,
    val comments: List<CommentEntity?>?
) : Comparable<PostEntity> {
    override fun compareTo(other: PostEntity): Int {
        return if (
            this.id == other.id &&
            this.title == other.title &&
            this.content == other.content &&
            this.imageUrls == other.imageUrls &&
            this.createdTime == other.createdTime &&
            this.lastModified == other.lastModified &&
            this.likes == other.likes &&
            this.comments == other.comments
        ) 0 else 1
    }
}

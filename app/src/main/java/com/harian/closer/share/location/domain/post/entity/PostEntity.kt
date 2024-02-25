package com.harian.closer.share.location.domain.post.entity

import com.bumptech.glide.load.model.GlideUrl
import com.harian.closer.share.location.domain.comment.entity.CommentEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity

data class PostEntity(
    val id: Int?,
    val title: String?,
    val content: String?,
    val images: List<ImageEntity>?,
    val createdTime: Long?,
    val lastModified: Long?,
    val owner: UserEntity?,
    val likes: List<UserEntity?>?,
    val comments: List<CommentEntity?>?,
    var isLiked: Boolean = false
) : Comparable<PostEntity> {
    override fun compareTo(other: PostEntity): Int {
        return if (
            this.id == other.id &&
            this.title == other.title &&
            this.content == other.content &&
            this.images == other.images &&
            this.createdTime == other.createdTime &&
            this.lastModified == other.lastModified &&
            this.likes == other.likes &&
            this.comments == other.comments &&
            this.isLiked == other.isLiked
        ) 0 else 1
    }
}

data class ImageEntity(
    val id: Int?,
    val url: String?,
    val comments: List<UserEntity>?,
    val likes: List<UserEntity>?,
    val authorizedUrl: GlideUrl?,
    var isLiked: Boolean = false
) : Comparable<ImageEntity> {
    override fun compareTo(other: ImageEntity): Int {
        return if (
            this.id == other.id &&
            this.url == other.url &&
            this.comments == other.comments &&
            this.likes == other.likes
        ) 0 else 1
    }
}

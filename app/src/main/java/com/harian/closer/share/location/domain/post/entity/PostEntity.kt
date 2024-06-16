package com.harian.closer.share.location.domain.post.entity

import android.os.Parcelable
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.harian.closer.share.location.domain.comment.entity.CommentEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.utils.Constants
import com.harian.software.closer.share.location.BuildConfig
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostEntity(
    val id: Int?,
    val title: String?,
    val content: String?,
    val images: List<ImageEntity>?,
    val createdTime: Long?,
    val lastModified: Long?,
    val owner: UserEntity?,
    val likes: List<UserEntity?>?,
    val watches: List<UserEntity?>?,
    val comments: List<CommentEntity?>?,
    var isLiked: Boolean = false
) : Comparable<PostEntity>, Parcelable {
    override fun compareTo(other: PostEntity): Int {
        return if (
            this.id == other.id &&
            this.title == other.title &&
            this.content == other.content &&
            this.images == other.images &&
            this.createdTime == other.createdTime &&
            this.lastModified == other.lastModified &&
            this.likes == other.likes &&
            this.watches == other.watches &&
            this.comments == other.comments &&
            this.isLiked == other.isLiked
        ) 0 else 1
    }
}

@Parcelize
data class ImageEntity(
    val id: Int?,
    val url: String?,
    val comments: List<UserEntity>?,
    val likes: List<UserEntity>?,
    var isLiked: Boolean = false
) : Comparable<ImageEntity>, Parcelable {

    @IgnoredOnParcel
    private var authorizedUrl: GlideUrl? = null

    fun getAuthorizeUrl(bearerToken: String): GlideUrl? {
        if (authorizedUrl == null) {
            authorizedUrl = GlideUrl(BuildConfig.API_BASE_URL + url, LazyHeaders.Builder().addHeader(Constants.AUTHORIZATION, bearerToken).build())
        }
        return authorizedUrl
    }

    override fun compareTo(other: ImageEntity): Int {
        return if (
            this.id == other.id &&
            this.url == other.url &&
            this.comments == other.comments &&
            this.likes == other.likes
        ) 0 else 1
    }
}

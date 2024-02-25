package com.harian.closer.share.location.utils

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.harian.closer.share.location.data.post.remote.dto.CommentResponse
import com.harian.closer.share.location.data.post.remote.dto.ImageResponse
import com.harian.closer.share.location.data.post.remote.dto.PostResponse
import com.harian.closer.share.location.data.user.remote.dto.UserResponse
import com.harian.closer.share.location.domain.comment.entity.CommentEntity
import com.harian.closer.share.location.domain.post.entity.ImageEntity
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.platform.SharedPrefs
import com.harian.software.closer.share.location.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResponseUtil @Inject constructor(private val sharedPrefs: SharedPrefs) {
    fun buildUserEntity(userResponse: UserResponse?): UserEntity {
        val avatarUrl = BuildConfig.API_BASE_URL + (userResponse?.avatar ?: Constants.DEFAULT_IMAGE_URL)
        val header = LazyHeaders.Builder().addHeader(Constants.AUTHORIZATION, sharedPrefs.getToken()).build()
        val authorizedAvatarUrl = GlideUrl(avatarUrl, header)

        return UserEntity(
            id = userResponse?.id,
            name = userResponse?.name,
            avatar = userResponse?.avatar,
            email = userResponse?.email,
            gender = userResponse?.gender,
            description = userResponse?.description,
            authorizedAvatarUrl = authorizedAvatarUrl
        )
    }

    fun buildPostEntity(postResponse: PostResponse?): PostEntity {
        val postOwner = postResponse?.owner?.let { buildUserEntity(it) }
        val images = postResponse?.images?.map { image ->
            buildImageEntity(image)
        }
        val likes = postResponse?.likes?.map { user ->
            buildUserEntity(user)
        }
        val comments = postResponse?.comments?.map { comment ->
            buildCommentEntity(comment)
        }
        return PostEntity(
            id = postResponse?.id,
            title = postResponse?.title,
            content = postResponse?.content,
            images = images,
            createdTime = postResponse?.createdTime,
            lastModified = postResponse?.lastModified,
            owner = postOwner,
            likes = likes,
            comments = comments
        )
    }

    fun buildImageEntity(imageResponse: ImageResponse): ImageEntity {
        val comments = imageResponse.comments?.map { user ->
            buildUserEntity(user)
        }

        val likes = imageResponse.likes?.map { user ->
            buildUserEntity(user)
        }

        val url = BuildConfig.API_BASE_URL + imageResponse.url
        val header = LazyHeaders.Builder().addHeader(Constants.AUTHORIZATION, sharedPrefs.getToken()).build()
        val authorizedUrl = GlideUrl(url, header)

        return ImageEntity(
            id = imageResponse.id,
            url = imageResponse.url,
            comments = comments,
            likes = likes,
            authorizedUrl = authorizedUrl
        )
    }

    fun buildCommentEntity(commentResponse: CommentResponse?): CommentEntity {
        val owner = commentResponse?.owner?.let { buildUserEntity(it) }
        return CommentEntity(
            id = commentResponse?.id,
            content = commentResponse?.content,
            createdTime = commentResponse?.createdTime,
            owner = owner,
        )
    }
}

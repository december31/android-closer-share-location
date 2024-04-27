package com.harian.closer.share.location.utils

import com.harian.closer.share.location.data.post.remote.dto.CommentResponse
import com.harian.closer.share.location.data.post.remote.dto.ImageResponse
import com.harian.closer.share.location.data.post.remote.dto.PostDTO
import com.harian.closer.share.location.data.user.remote.dto.DeviceDTO
import com.harian.closer.share.location.data.user.remote.dto.FriendRequestDTO
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import com.harian.closer.share.location.domain.comment.entity.CommentEntity
import com.harian.closer.share.location.domain.post.entity.ImageEntity
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.domain.user.entity.DeviceEntity
import com.harian.closer.share.location.domain.user.entity.FriendRequestEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResponseUtil @Inject constructor() {
    fun buildUserEntity(userResponse: UserDTO?): UserEntity {
        return UserEntity(
            id = userResponse?.id,
            name = userResponse?.name,
            avatar = userResponse?.avatar ?: Constants.DEFAULT_IMAGE_URL,
            email = userResponse?.email,
            gender = userResponse?.gender,
            isFriend = userResponse?.isFriend,
            status = userResponse?.status,
            description = userResponse?.description,
            latitude = userResponse?.latitude,
            longitude = userResponse?.longitude
        )
    }

    fun buildPostEntity(postResponse: PostDTO?): PostEntity {
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

    fun buildFriendRequestEntity(friendRequestDTO: FriendRequestDTO): FriendRequestEntity {
        return FriendRequestEntity(
            requestor = buildUserEntity(friendRequestDTO.requestor),
            since = friendRequestDTO.since,
            status = friendRequestDTO.status
        )
    }

    private fun buildImageEntity(imageResponse: ImageResponse): ImageEntity {
        val comments = imageResponse.comments?.map { user ->
            buildUserEntity(user)
        }

        val likes = imageResponse.likes?.map { user ->
            buildUserEntity(user)
        }

        return ImageEntity(
            id = imageResponse.id,
            url = imageResponse.url,
            comments = comments,
            likes = likes,
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

    fun buildDeviceEntity(deviceDTO: DeviceDTO?): DeviceEntity {
        return DeviceEntity(
            id = deviceDTO?.id,
            model = deviceDTO?.model,
            manufacturer = deviceDTO?.manufacturer,
            brand = deviceDTO?.brand,
            type = deviceDTO?.type,
            versionCodeBase = deviceDTO?.versionCodeBase,
            incremental = deviceDTO?.incremental,
            sdk = deviceDTO?.sdk,
            board = deviceDTO?.board,
            host = deviceDTO?.host,
            fingerprint = deviceDTO?.fingerprint,
            versionCode = deviceDTO?.versionCode,
            firebaseMessagingToken = deviceDTO?.firebaseMessagingToken
        )
    }
}

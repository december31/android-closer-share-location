package com.harian.closer.share.location.domain.user.entity

import com.harian.closer.share.location.data.user.remote.dto.Status

data class FriendRequestEntity(
    val requestor: UserEntity,
    val since: Long,
    val status: Status
)

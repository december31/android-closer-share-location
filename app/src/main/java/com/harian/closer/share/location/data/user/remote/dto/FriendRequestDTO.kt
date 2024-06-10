package com.harian.closer.share.location.data.user.remote.dto

import com.harian.closer.share.location.data.common.base.BaseDTO
import com.harian.closer.share.location.domain.user.entity.FriendRequestEntity

data class FriendRequestDTO(
    val requestor: UserDTO,
    val since: Long,
    val status: Status
) : BaseDTO<FriendRequestEntity> {
    override fun toEntity(): FriendRequestEntity {
        return FriendRequestEntity(
            requestor = requestor.toEntity(),
            since = since,
            status = status
        )
    }
}

enum class Status {
    ACCEPTED,
    PENDING,
    DENIED
}

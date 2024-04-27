package com.harian.closer.share.location.data.user.remote.dto

data class FriendRequestDTO(
    val requestor: UserDTO,
    val since: Long,
    val status: Status
)

enum class Status {
    ACCEPTED,
    PENDING,
    DENIED
}

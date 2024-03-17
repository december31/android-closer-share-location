package com.harian.closer.share.location.domain.user.entity

data class ProfileEntity<T>(
    var data: T?,
    val type: ProfileType
)

enum class ProfileType(val viewType: Int) {
    PROFILE(0),
    FRIENDS(1),
    POSTS(2)
}

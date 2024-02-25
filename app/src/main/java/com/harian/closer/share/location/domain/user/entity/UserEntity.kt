package com.harian.closer.share.location.domain.user.entity

import com.bumptech.glide.load.model.GlideUrl

data class UserEntity(
    val id: Int?,
    val name: String?,
    val email: String?,
    val avatar: String?,
    val gender: String?,
    val description: String?,
    val authorizedAvatarUrl: GlideUrl
)

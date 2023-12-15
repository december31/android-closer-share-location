package com.harian.closer.share.location.domain.login.entity

import com.harian.closer.share.location.data.common.utils.Token

data class LoginEntity (
    val id: Int?,
    val name: String?,
    val email: String?,
    var gender: String?,
    var description: String?,
    val token: Token?,
)

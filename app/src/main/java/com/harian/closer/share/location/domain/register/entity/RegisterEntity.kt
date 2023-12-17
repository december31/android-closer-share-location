package com.harian.closer.share.location.domain.register.entity

import com.harian.closer.share.location.data.common.utils.Token

data class RegisterEntity (
    val id: Int?,
    val name: String?,
    val email: String?,
    var gender: String?,
    var description: String?,
    val token: Token?,
)

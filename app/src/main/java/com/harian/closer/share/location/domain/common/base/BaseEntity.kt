package com.harian.closer.share.location.domain.common.base

interface BaseEntity<DTO> {
    fun toDTO(): DTO
}

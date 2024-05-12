package com.harian.closer.share.location.data.common.base

interface BaseDTO<Entity> {
    fun toEntity(): Entity
}

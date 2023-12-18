package com.harian.closer.share.location.domain.common.base

import kotlin.random.Random

sealed class BaseResult<out T : Any, out U : Any> {
    class Success<T : Any>(val data: T) : BaseResult<T, Nothing>()
    class Error<U : Any>(val rawResponse: U) : BaseResult<Nothing, U>()

    override fun equals(other: Any?): Boolean {
        return false
    }

    override fun hashCode(): Int {
        return Random.nextInt()
    }
}

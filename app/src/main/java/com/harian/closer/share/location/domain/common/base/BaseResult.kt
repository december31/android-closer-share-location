package com.harian.closer.share.location.domain.common.base

sealed class BaseResult<out T : Any, out U : Any> {
    class Success<T : Any>(val data: T) : BaseResult<T, Nothing>()
    class Error<U : Any>(val rawResponse: U) : BaseResult<Nothing, U>()
}

package com.harian.closer.share.location.utils.extension

fun <T> List<T>.clone(): List<T> {
    return arrayListOf<T>().apply { addAll(this@clone) }
}

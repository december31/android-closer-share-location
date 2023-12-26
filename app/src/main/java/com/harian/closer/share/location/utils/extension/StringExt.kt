package com.harian.closer.share.location.utils.extension

import android.util.Patterns

fun String.isEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.toBearerToken(): String {
    return "Bearer $this"
}

package com.harian.closer.share.location.utils

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun runOnMainThread(block: () -> Unit) {
    withContext(Dispatchers.Main) {
        block.invoke()
    }
}

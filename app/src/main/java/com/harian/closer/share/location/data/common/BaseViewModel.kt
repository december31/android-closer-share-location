package com.harian.closer.share.location.data.common

import android.util.Log
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {
    fun logError(error: String) {
        Log.e(this.javaClass.simpleName, error)
    }

    fun logDebug(message: String) {
        Log.d(this.javaClass.simpleName, message)
    }
}

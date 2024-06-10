package com.harian.closer.share.location.data.common

import android.util.Log
import androidx.lifecycle.ViewModel
import com.harian.closer.share.location.utils.runOnMainThread
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class BaseViewModel : ViewModel() {
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> get() = _loadingState

    protected suspend fun showLoading() {
        runOnMainThread {
            _loadingState.value = true
        }
    }

    protected suspend fun hideLoading() {
        runOnMainThread {
            _loadingState.value = false
        }
    }

    fun logError(error: String) {
        Log.e(this.javaClass.simpleName, error)
    }

    fun logDebug(message: String) {
        Log.d(this.javaClass.simpleName, message)
    }
}

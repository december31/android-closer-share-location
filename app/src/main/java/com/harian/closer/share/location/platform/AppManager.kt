package com.harian.closer.share.location.platform

import android.os.SystemClock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppManager @Inject constructor() {
    private var backPressedTime = 0L
    val isBackPressFinish: Boolean
        get() {
            // preventing finish, using threshold of 2000 ms
            if (backPressedTime + 2000 > SystemClock.elapsedRealtime()) {
                return true
            }

            backPressedTime = SystemClock.elapsedRealtime()
            return false
        }

}

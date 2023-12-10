package com.bkplus.hitranslator.app.platform

import android.app.Application
import com.harian.closer.share.location.platform.BasePrefers
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        BasePrefers.initPrefs(applicationContext)
    }
}

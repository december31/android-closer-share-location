package com.harian.closer.share.location.platform

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.edit

class BasePrefers(context: Context) {

    private val prefsNewUser = "prefsNewUser"
    private val prefsLocale = "prefsLocale"
    private val prefsIsShowInterSplash = "prefsIsShowInterSplash"
    private val prefsIsShowAdsSplash = "prefsIsShowAdsSplash2"
    private val prefsIsShowNativeLanguage = "prefsIsShowNativeLanguage"
    private val prefsIsShowAdsResume = "prefsIsShowAdsResume"
    private val prefsIsShowNativeOnboarding = "prefsIsShowNativeOnboarding"
    private val prefsIsShowInterGetStart = "prefsIsShowInterGetStart"
    private val prefsIsShowBanner = "prefsIsShowBanner"
    private val prefsIsShowNativeHome = "prefsIsShowNativeHome"
    private val prefsIsShowInterHome1 = "prefsIsShowInterHome1"
    private val prefsIsShowInterVideoCall = "prefsIsShowInterVideoCall2"
    private val prefsIsShowInterVoiceCall = "prefsIsShowInterVoiceCall2"
    private val prefsIsShowInterVideoCallFake = "prefsIsShowInterVideoCallFake"
    private val prefsIsShowInterHome2 = "prefsIsShowInterHome2"
    private val prefsDoneOnb = "prefsDoneOnb"
    private val prefsVideoCallHalloweenView= "prefsVideoCallHalloweenView"
    private val prefsIsShowInterMessagesCelerity = "prefsIsShowInterMessagesCelerity"
    private val prefsVideoCallIdolView = "prefsVideoCallIdolView"
    private val prefsMessagesHalloweenView = "prefsMessagesHalloweenView"
    private val prefsCamera = "prefsCamera"

    private val mPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    var newUser
        get() = mPrefs.getBoolean(prefsNewUser, true)
        set(value) = mPrefs.edit { putBoolean(prefsNewUser, value) }
    var doneOnb
        get() = mPrefs.getBoolean(prefsDoneOnb, false)
        set(value) = mPrefs.edit { putBoolean(prefsDoneOnb, value) }
    var locale
        get() = mPrefs.getString(prefsLocale, "en")
        set(value) = mPrefs.edit { putString(prefsLocale, value) }

    var isShowInterSplash
        get() = mPrefs.getBoolean(prefsIsShowInterSplash, true)
        set(value) = mPrefs.edit { putBoolean(prefsIsShowInterSplash, value) }
    var isShowAdsSplash
        get() = mPrefs.getString(prefsIsShowAdsSplash, "new")
        set(value) = mPrefs.edit { putString(prefsIsShowAdsSplash, value) }
    var isShowNativeLanguage
        get() = mPrefs.getBoolean(prefsIsShowNativeLanguage, true)
        set(value) = mPrefs.edit { putBoolean(prefsIsShowNativeLanguage, value) }
    var isShowAdsResume
        get() = mPrefs.getBoolean(prefsIsShowAdsResume, true)
        set(value) = mPrefs.edit { putBoolean(prefsIsShowAdsResume, value) }
    var isShowNativeOnboarding
        get() = mPrefs.getBoolean(prefsIsShowNativeOnboarding, true)
        set(value) = mPrefs.edit { putBoolean(prefsIsShowNativeOnboarding, value) }
    var isShowInterGetStart
        get() = mPrefs.getBoolean(prefsIsShowInterGetStart, true)
        set(value) = mPrefs.edit { putBoolean(prefsIsShowInterGetStart, value) }
    var isShowBanner
        get() = mPrefs.getBoolean(prefsIsShowBanner, true)
        set(value) = mPrefs.edit { putBoolean(prefsIsShowBanner, value) }
    var isShowNativeHome
        get() = mPrefs.getBoolean(prefsIsShowNativeHome, true)
        set(value) = mPrefs.edit { putBoolean(prefsIsShowNativeHome, value) }
    var isShowInterVideoCallFake
        get() = mPrefs.getBoolean(prefsIsShowInterVideoCallFake, true)
        set(value) = mPrefs.edit { putBoolean(prefsIsShowInterVideoCallFake, value) }
    var isShowInterVoiceCall
        get() = mPrefs.getString(prefsIsShowInterVideoCall, "new")
        set(value) = mPrefs.edit { putString(prefsIsShowInterVideoCall, value) }
    var isShowInterMessagesCelerity
        get() = mPrefs.getBoolean(prefsIsShowInterMessagesCelerity, true)
        set(value) = mPrefs.edit { putBoolean(prefsIsShowInterMessagesCelerity, value) }
    var videoCallIdolView
        get() = mPrefs.getString(prefsVideoCallIdolView, "on")
        set(value) = mPrefs.edit { putString(prefsVideoCallIdolView, value) }
    var videoCallHalloweenView
        get() = mPrefs.getString(prefsVideoCallHalloweenView, "on")
        set(value) = mPrefs.edit { putString(prefsVideoCallHalloweenView, value) }
    var messagesHalloweenView
        get() = mPrefs.getString(prefsMessagesHalloweenView, "on")
        set(value) = mPrefs.edit { putString(prefsMessagesHalloweenView, value) }
    var isShowInterHome1
        get() = mPrefs.getBoolean(prefsIsShowInterHome1, true)
        set(value) = mPrefs.edit { putBoolean(prefsIsShowInterHome1, value) }
    var isShowInterHome2
        get() = mPrefs.getBoolean(prefsIsShowInterHome2, true)
        set(value) = mPrefs.edit { putBoolean(prefsIsShowInterHome2, value) }
    var isCamera
        get() = mPrefs.getBoolean(prefsCamera, false)
        set(value) = mPrefs.edit { putBoolean(prefsCamera, value) }

    companion object {
        @Volatile
        private var INSTANCE: BasePrefers? = null

        fun initPrefs(context: Context): BasePrefers {
            return INSTANCE ?: synchronized(this) {
                val instance = BasePrefers(context)
                INSTANCE = instance
                // return instance
                instance
            }
        }

        fun getPrefsInstance(): BasePrefers {
            return INSTANCE ?: error("GoPreferences not initialized!")
        }
    }
}

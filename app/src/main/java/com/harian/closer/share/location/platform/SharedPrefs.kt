package com.harian.closer.share.location.platform

import android.content.Context
import android.content.SharedPreferences
import com.harian.closer.share.location.data.common.utils.Token
import com.harian.closer.share.location.utils.extension.toBearerToken

@Suppress("UNCHECKED_CAST")
class SharedPrefs(context: Context) {
    companion object {
        private const val PREF = "MyAppPrefName"
        private const val PREF_TOKEN = "user_token"
        private const val PREF_REFRESH_TOKEN = "user_refresh_token"
        private const val PREF_IS_RESETTING_PASSWORD = "is_resetting_password"
    }

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)


    fun saveToken(token: Token?) {
        if (token != null) {
            put(PREF_TOKEN, token.accessToken)
            put(PREF_REFRESH_TOKEN, token.refreshToken)
        }
    }

    fun getToken(): String {
        return get(PREF_TOKEN, String::class.java).toBearerToken()
    }

    fun getRefreshToken(): String {
        return get(PREF_REFRESH_TOKEN, String::class.java)
    }

    var needResetPassword: Boolean
        get() = get(PREF_IS_RESETTING_PASSWORD, Boolean::class.java)
        set(value) = put(PREF_IS_RESETTING_PASSWORD, value)

    private fun <T> get(key: String, clazz: Class<T>): T =
        when (clazz) {
            String::class.java -> sharedPref.getString(key, "")
            Boolean::class.java -> sharedPref.getBoolean(key, false)
            Float::class.java -> sharedPref.getFloat(key, -1f)
            Double::class.java -> sharedPref.getFloat(key, -1f)
            Int::class.java -> sharedPref.getInt(key, -1)
            Long::class.java -> sharedPref.getLong(key, -1L)
            else -> null
        } as T

    private fun <T> put(key: String, data: T) {
        val editor = sharedPref.edit()
        when (data) {
            is String -> editor.putString(key, data)
            is Boolean -> editor.putBoolean(key, data)
            is Float -> editor.putFloat(key, data)
            is Double -> editor.putFloat(key, data.toFloat())
            is Int -> editor.putInt(key, data)
            is Long -> editor.putLong(key, data)
        }
        editor.apply()
    }

    fun clearTokens() {
        sharedPref.edit().run {
            remove(PREF_TOKEN)
            remove(PREF_REFRESH_TOKEN)
        }.apply()
    }
}

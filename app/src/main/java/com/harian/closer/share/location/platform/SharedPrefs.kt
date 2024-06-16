package com.harian.closer.share.location.platform

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.android.gms.maps.GoogleMap
import com.harian.closer.share.location.data.common.utils.Token
import com.harian.closer.share.location.utils.extension.toBearerToken

@Suppress("UNCHECKED_CAST")
class SharedPrefs(context: Context) {
    companion object {
        private const val PREF = "MyAppPrefName"
        private const val PREF_TOKEN = "user_token"
        private const val PREF_REFRESH_TOKEN = "user_refresh_token"
        private const val PREF_IS_RESETTING_PASSWORD = "is_resetting_password"
        private const val PREF_MAP_TYPE = "map_type"
    }

    private var sharedPref: SharedPreferences

    init {
        val masterKey: MasterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPref = EncryptedSharedPreferences.create(
            context,
            "secret_shared_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

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

    private fun <T> get(key: String, clazz: Class<T>, default: T? = null): T =
        when (clazz) {
            String::class.java -> sharedPref.getString(key, "")
            Boolean::class.java -> sharedPref.getBoolean(key, (default ?: false) as Boolean)
            Float::class.java -> sharedPref.getFloat(key, (default ?: -1f) as Float)
            Double::class.java -> sharedPref.getFloat(key, (default ?: -1f) as Float)
            Int::class.java -> sharedPref.getInt(key, (default ?: -1) as Int)
            Long::class.java -> sharedPref.getLong(key, (default ?: -1L) as Long)
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

    fun setMapType(mapType: Int) {
        put(PREF_MAP_TYPE, mapType)
    }

    fun getMapType(): Int {
        return get(PREF_MAP_TYPE, Int::class.java, GoogleMap.MAP_TYPE_NORMAL)
    }
}

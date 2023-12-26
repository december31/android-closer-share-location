package com.harian.closer.share.location.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.harian.software.closer.share.location.BuildConfig
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


fun getBitmapFromURL(sourceUrl: String?, token: String? = null): Bitmap? {
    return try {
        Log.e("getting bitmap from url", (BuildConfig.API_BASE_URL + sourceUrl))
        val bearerToken = "Bearer $token"
        val url = URL(BuildConfig.API_BASE_URL + sourceUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty(Constants.AUTHORIZATION, bearerToken)
        connection.setRequestProperty(Constants.CONTENT_TYPE, "image/*")
        connection.doInput = true
        connection.connect()
        Log.e("status", connection.responseCode.toString())
        val input = connection.inputStream
        val myBitmap = BitmapFactory.decodeStream(input)
        Log.e("Bitmap", "returned")
        myBitmap
    } catch (e: IOException) {
        e.printStackTrace()
        Log.e("Exception", e.message.toString())
        null
    }
}

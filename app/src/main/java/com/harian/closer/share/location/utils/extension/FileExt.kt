package com.harian.closer.share.location.utils.extension

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

/**
 * create a bitmap from image file
 * @param width width in dp of bitmap
 * @param height height in dp of bitmap
 */
fun File.toBitmap(width: Int, height: Int): Bitmap {

    val bmOptions = BitmapFactory.Options()
    val bitmap = BitmapFactory.decodeFile(this.absolutePath, bmOptions)
    return Bitmap.createScaledBitmap(bitmap, width.dp, height.dp, true)
}

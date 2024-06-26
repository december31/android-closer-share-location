@file:Suppress("unused")

package com.harian.closer.share.location.utils.extension

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.children
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.goneAllChildView() {
    if (this is ViewGroup) {
        this.children.forEach {
            it.goneAllChildView()
        }
    } else {
        this.gone()
    }
}

fun View.invisibleAllChildView() {
    if (this is ViewGroup) {
        this.children.forEach {
            it.invisibleAllChildView()
        }
    } else {
        this.invisible()
    }
}

fun View.invisibleAllChildViewIf(condition: (View) -> Boolean) {
    if (condition.invoke(this)) {
        this.invisible()
    } else {
        (this as? ViewGroup)?.children?.forEach {
            it.invisibleAllChildViewIf(condition)
        }
    }
}

fun View.toBitmap(): Bitmap {
    this.measure(
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
    this.layout(0, 0, this.measuredWidth, this.measuredHeight);


    val returnedBitmap = Bitmap.createBitmap(this.measuredWidth, this.measuredHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(returnedBitmap)
    val bgDrawable = this.background
    if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.TRANSPARENT)
    this.draw(canvas)
    return returnedBitmap
}

fun ImageView.glideLoadImage(resId: Int) {
    Glide.with(this.context)
        .load(resId)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

fun ImageView.glideLoadImage(uri: Uri?) {
    Glide.with(this.context)
        .load(uri)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

fun ImageView.glideLoadImage(url: GlideUrl?) {
    Glide.with(this.context)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

fun ImageView.glideLoadImage(bitmap: Bitmap?) {
    Glide.with(this.context)
        .load(bitmap)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

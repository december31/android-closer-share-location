package com.harian.closer.share.location.utils.extension

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

fun ImageView.glideLoadImage(resId: Int) {
    Glide.with(this.context)
        .load(resId)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

fun ImageView.glideLoadImage(url: GlideUrl) {
    Glide.with(this.context)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

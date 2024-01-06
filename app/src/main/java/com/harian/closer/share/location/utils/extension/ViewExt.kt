package com.harian.closer.share.location.utils.extension

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children

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

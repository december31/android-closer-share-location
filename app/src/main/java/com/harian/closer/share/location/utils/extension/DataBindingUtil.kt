package com.harian.closer.share.location.utils.extension

import androidx.constraintlayout.widget.Guideline
import androidx.databinding.BindingAdapter

@BindingAdapter("guidePercent")
fun setGuideLineRatio(guideline: Guideline, value: Float) {
    guideline.setGuidelinePercent(value)
}

package com.harian.closer.share.location.presentation.common.custom.imageview.state

import com.harian.closer.share.location.presentation.common.custom.imageview.MultipleImagesView

abstract class State(protected val multipleImagesView: MultipleImagesView) {

    abstract fun setupUI()
}

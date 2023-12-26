package com.harian.closer.share.location.presentation.common.custom.imageview.state

import com.harian.closer.share.location.presentation.common.custom.imageview.MultipleImagesView
import com.harian.closer.share.location.utils.extension.goneAllChildView
import com.harian.closer.share.location.utils.extension.visible

class TwoImagesState(multipleImagesView: MultipleImagesView) : State(multipleImagesView) {
    override fun setupUI() {
        multipleImagesView.getBinding().apply {
            container.goneAllChildView()
            image1.visible()
            image2.visible()
            guideLine70.setGuidelinePercent(1f)
            horizontalGuideLine50.setGuidelinePercent(0.5f)
        }
    }
}

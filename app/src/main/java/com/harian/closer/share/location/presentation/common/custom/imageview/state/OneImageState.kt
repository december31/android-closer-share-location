package com.harian.closer.share.location.presentation.common.custom.imageview.state

import com.harian.closer.share.location.presentation.common.custom.imageview.MultipleImagesView
import com.harian.closer.share.location.utils.extension.goneAllChildView
import com.harian.closer.share.location.utils.extension.visible

class OneImageState(multipleImagesView: MultipleImagesView) : State(multipleImagesView) {
    override fun setupUI() {
        multipleImagesView.getBinding().apply {
            container.goneAllChildView()

            horizontalGuideLine50.setGuidelinePercent(1f)
            guideLine70.setGuidelinePercent(1f)
            image1.visible()
        }
    }
}

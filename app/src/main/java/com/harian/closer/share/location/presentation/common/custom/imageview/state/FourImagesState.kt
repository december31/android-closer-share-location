package com.harian.closer.share.location.presentation.common.custom.imageview.state

import com.harian.closer.share.location.presentation.common.custom.imageview.MultipleImagesView
import com.harian.closer.share.location.utils.extension.goneAllChildView
import com.harian.closer.share.location.utils.extension.visible

class FourImagesState(multipleImagesView: MultipleImagesView) : State(multipleImagesView) {
    override fun setupUI() {
        multipleImagesView.getBinding().apply {
            container.goneAllChildView()
            image1.visible()
            image2.visible()
            image3.visible()
            image4.visible()
            verticalGuideLine50.setGuidelinePercent(0.5f)
            guideLine70.setGuidelinePercent(0.5f)
            guideLine33.setGuidelinePercent(0.5f)
            guideLine66.setGuidelinePercent(1f)
        }
    }
}

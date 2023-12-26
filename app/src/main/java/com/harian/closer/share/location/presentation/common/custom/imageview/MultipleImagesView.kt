package com.harian.closer.share.location.presentation.common.custom.imageview

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.harian.closer.share.location.utils.extension.goneAllChildView
import com.harian.closer.share.location.utils.extension.invisibleAllChildView
import com.harian.closer.share.location.utils.extension.visible
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.LayoutMultipleImagesViewBinding

class MultipleImagesView : RelativeLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var binding: LayoutMultipleImagesViewBinding =
        LayoutMultipleImagesViewBinding.inflate(LayoutInflater.from(context), this, true)

    private var onImageClickListener: OnImageClickListener? = null

    fun setOnImageClickListener(onImageClickListener: OnImageClickListener) {
        this.onImageClickListener = onImageClickListener
    }

    fun loadImages(authorizedUrls: List<Any?>) {
        binding.apply {
            val imagesCount = authorizedUrls.size
            val imagesIterator = authorizedUrls.iterator()
            container.invisibleAllChildView()

            remainImagesCount.isVisible = imagesCount > 5
            remainImagesCount.text = context.getString(R.string.remain_image_count, imagesCount - 5)

            when (imagesCount) {
                0 -> Unit
                1 -> {
                    image1.visible()
                    bindImage(imagesIterator, image1)
                }

                2 -> {
                    image2.visible()
                    image3.visible()
                    bindImage(imagesIterator, image2)
                    bindImage(imagesIterator, image3)
                }

                3 -> {
                    image4.visible()
                    image5.visible()
                    image6.visible()
                    bindImage(imagesIterator, image4)
                    bindImage(imagesIterator, image5)
                    bindImage(imagesIterator, image6)
                }

                4 -> {
                    image7.visible()
                    image8.visible()
                    image9.visible()
                    image10.visible()
                    bindImage(imagesIterator, image7)
                    bindImage(imagesIterator, image8)
                    bindImage(imagesIterator, image9)
                    bindImage(imagesIterator, image10)
                }

                else -> {
                    image11.visible()
                    image12.visible()
                    image13.visible()
                    image14.visible()
                    image15.visible()
                    bindImage(imagesIterator, image11)
                    bindImage(imagesIterator, image12)
                    bindImage(imagesIterator, image13)
                    bindImage(imagesIterator, image14)
                    bindImage(imagesIterator, image15)
                }
            }
        }
    }

    private fun bindImage(imagesIterator: Iterator<Any?>, imageView: ImageView) {
        if (imagesIterator.hasNext()) {
            Glide.with(context)
                .load(imagesIterator.next())
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.image_loading)
                .into(imageView)
        }
    }

    interface OnImageClickListener {
        fun onImageClick(uri: Uri)
    }
}


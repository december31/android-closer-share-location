package com.harian.closer.share.location.presentation.common.custom.imageview

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.LayoutMultipleImagesViewBinding

class MultipleImagesView : RelativeLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    private var binding: LayoutMultipleImagesViewBinding =
        LayoutMultipleImagesViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun getBinding(): LayoutMultipleImagesViewBinding {
        return binding
    }

    private var onImageClickListener: OnImageClickListener? = null

    fun setOnImageClickListener(onImageClickListener: OnImageClickListener) {
        this.onImageClickListener = onImageClickListener
    }

    fun loadImages(authorizedUrls: List<Any?>) {
        binding.apply {
            imagesCount = authorizedUrls.size
            remainImagesCount.text = context.getString(R.string.remain_image_count, authorizedUrls.size - 5)
            val imagesIterator = authorizedUrls.iterator()

            if (imagesCount >= 1) {
                bindImage(imagesIterator, image1)
            }
            if (imagesCount >= 2 && imagesCount != 3) {
                bindImage(imagesIterator, image2)
            }
            if (imagesCount == 3) {
                bindImage(imagesIterator, image6)
                bindImage(imagesIterator, image7)
            }
            if (imagesCount >= 4) {
                bindImage(imagesIterator, image3)
                bindImage(imagesIterator, image4)
            }
            if (imagesCount >= 5) {
                bindImage(imagesIterator, image5)
            }
        }
    }

    private fun bindImage(imagesIterator: Iterator<Any?>, imageView: ImageView) {
        if (imagesIterator.hasNext()) {
            Glide.with(context)
                .load(imagesIterator.next())
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.image_loading)
                .into(imageView)
        }
    }

    interface OnImageClickListener {
        fun onImageClick(uri: Uri)
    }
}


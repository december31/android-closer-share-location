package com.harian.closer.share.location.presentation.setting.avatar

import android.graphics.Bitmap
import android.net.Uri
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.harian.closer.share.location.platform.BaseFullScreenDialogFragment
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentCropImageBinding

class CropImageDialog : BaseFullScreenDialogFragment<FragmentCropImageBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_crop_image

    private var uri: Uri? = null
    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun setUri(uri: Uri) {
        this.uri = uri
    }

    override fun setupUI() {
        binding.cropImageView.setImageCropOptions(CropImageOptions().apply {
            aspectRatioX = 1
            aspectRatioY = 1
            guidelines = CropImageView.Guidelines.OFF
            fixAspectRatio = true
        })

        binding.cropImageView.setImageUriAsync(uri)
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            btnSelect.setOnClickListener {
                listener?.onSelect(cropImageView.getCroppedImage())
                dismiss()
            }
        }
    }

    interface Listener {
        fun onSelect(bitmap: Bitmap?)
    }
}

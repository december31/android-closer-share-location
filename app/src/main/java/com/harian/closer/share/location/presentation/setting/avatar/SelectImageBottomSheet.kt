package com.harian.closer.share.location.presentation.setting.avatar

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.harian.closer.share.location.platform.BaseBottomSheetDialogFragment
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.BottomSheetSelectImageBinding

class SelectImageBottomSheet : BaseBottomSheetDialogFragment<BottomSheetSelectImageBinding>() {
    override val layoutId: Int
        get() = R.layout.bottom_sheet_select_image

    private var listener: Listener? = null
    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun setupUI() {
        super.setupUI()
        binding.lnContainer.clipToOutline = true
        dialog?.setOnShowListener {
            val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
            bottomSheetBehavior.isHideable = false
            BottomSheetBehavior.from(binding.containerRoot.parent as View).peekHeight =
                binding.containerRoot.height
            bottomSheetBehavior.peekHeight = binding.containerRoot.height
            binding.containerRoot.parent.requestLayout()
        }
    }


    override fun setupListener() {
        super.setupListener()
        binding.apply {
            btnCancel.setOnClickListener {
                dismiss()
            }
            btnCamera.setOnClickListener {
                listener?.onTakePicture()
            }
            btnSelectFromAlbum.setOnClickListener {
                listener?.onSelectFromAlbum()
            }
        }
    }

    interface Listener {
        fun onTakePicture()
        fun onSelectFromAlbum()
    }
}

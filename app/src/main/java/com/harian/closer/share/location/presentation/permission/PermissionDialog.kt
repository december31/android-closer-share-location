package com.harian.closer.share.location.presentation.permission

import com.harian.closer.share.location.platform.BaseDialogFragment
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.DialogPermissionBinding

class PermissionDialog : BaseDialogFragment<DialogPermissionBinding>() {
    override val layoutId: Int
        get() = R.layout.dialog_permission

    private var listener: Listener? = null
    private var permission: Permission? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun setData(permission: Permission) {
        this.permission = permission
    }

    override fun setupListener() {
        super.setupListener()
        binding.btnAccept.setOnClickListener {
            listener?.onAccept()
            dismiss()
        }
        binding.btnDecline.setOnClickListener {
            listener?.onDecline()
            dismiss()
        }
    }

    override fun setupUI() {
        super.setupUI()
        permission?.let {
            binding.tvTitle.setText(it.titleRes)
            binding.tvDescription.setText(it.descriptionRes)
            binding.imgIcon.setImageResource(it.iconRes)
        }
    }

    interface Listener {
        fun onAccept()
        fun onDecline()
    }
}

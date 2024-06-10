package com.harian.closer.share.location.presentation.widget

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.harian.closer.share.location.platform.BaseFullScreenDialogFragment
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.DialogFragmentLoadingBinding

class LoadingDialog : BaseFullScreenDialogFragment<DialogFragmentLoadingBinding>() {
    override val layoutId: Int
        get() = R.layout.dialog_fragment_loading
}

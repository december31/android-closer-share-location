package com.harian.closer.share.location.platform

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.harian.software.closer.share.location.R

abstract class BaseBottomSheetDialogFragment<T : ViewDataBinding> : BottomSheetDialogFragment() {

    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setOnShowListener {
            val bottomSheetBehavior = (dialog as BottomSheetDialog).behavior
            val density = Resources.getSystem().displayMetrics.density
            bottomSheetBehavior.peekHeight = (400 * density).toInt()
            bottomSheetBehavior.expandedOffset = (120 * density).toInt()
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }

        setupSystemBarBehavior()
        setupData()
        setupUI()
        handleStateChanges()
        setupListener()
    }

    protected abstract val layoutId: Int
    private var _binding: T? = null
    protected val binding: T get() = _binding!!

    protected open fun setupUI() {}
    protected open fun handleStateChanges() {}
    protected open fun setupData() {}
    protected open fun setupListener() {}

    @SuppressLint("WrongConstant")
    protected open fun setupSystemBarBehavior() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val insets = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.statusBars())
                binding.root.setPadding(0, insets.top, 0, insets.bottom)
            }
            WindowInsetsCompat.CONSUMED
        }
    }

    fun showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
        toast?.cancel()
        toast = Toast.makeText(context, message, length)
        toast?.show()
    }

    fun showToast(stringRes: Int, length: Int = Toast.LENGTH_SHORT) {
        toast?.cancel()
        toast = Toast.makeText(context, context?.getString(stringRes), length)
        toast?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

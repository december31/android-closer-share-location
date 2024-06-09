package com.harian.closer.share.location.platform

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

abstract class BaseDialogFragment<T : ViewDataBinding> : DialogFragment() {
    val mTag: String = this.javaClass.simpleName
    var isFullWidth = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.let {
            it.setBackgroundDrawableResource(android.R.color.transparent)
            WindowCompat.setDecorFitsSystemWindows(it, true)
            WindowInsetsControllerCompat(it, binding.root).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            if (isFullWidth) {
                val width = ViewGroup.LayoutParams.MATCH_PARENT
                val height = ViewGroup.LayoutParams.WRAP_CONTENT
                it.setGravity(Gravity.CENTER_VERTICAL)
                it.setLayout(width, height)
            }

        }
        setupData()
        setupUI()
        setupListener()
    }

    protected abstract val layoutId: Int
    protected lateinit var binding: T

    protected open fun setupUI() {}
    protected open fun setupData() {}
    protected open fun setupListener() {}
    fun show(manager: FragmentManager) {
        if (this.isAdded) {
            return
        } else Log.e(mTag, "$mTag isAdded")
        try {
            val fragmentTransaction = manager.beginTransaction()
            fragmentTransaction.add(this, mTag)
            fragmentTransaction.commit()
        } catch (e: Exception) {
            Log.d(mTag, "Show Dialog Error: $mTag")
        }
    }
}

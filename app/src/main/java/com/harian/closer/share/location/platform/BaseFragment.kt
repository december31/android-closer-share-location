package com.harian.closer.share.location.platform

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<T : ViewDataBinding> : Fragment() {

    private var toast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSystemBarBehavior()
        setupData()
        setupUI()
        setupListener()
    }

    protected abstract val layoutId: Int
    private var _binding: T? = null
    protected val binding: T get() = _binding!!

    protected open fun setupUI() {}

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

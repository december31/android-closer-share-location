package com.harian.closer.share.location.platform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.harian.closer.share.location.databinding.FragmentHomeBinding

abstract class BaseFragment<T : ViewDataBinding> : Fragment() {

    private var _binding: T? = null

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

        setupData()
        setupUI()
        setupListener()
    }

    protected abstract val layoutId: Int
    protected lateinit var binding: T

    protected open fun setupUI() {}
    protected open fun setupData() {}
    protected open fun setupListener() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

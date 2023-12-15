package com.harian.closer.share.location.presentation.home

import androidx.activity.OnBackPressedCallback
import com.harian.closer.share.location.R
import com.harian.closer.share.location.databinding.FragmentHomeBinding
import com.harian.closer.share.location.platform.BaseFragment
import kotlin.system.exitProcess

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_home

    override fun setupUI() {
        super.setupUI()
        handleOnBackPressed()
    }

    private fun handleOnBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finishAffinity()
                exitProcess(0)
            }
        })
    }
}

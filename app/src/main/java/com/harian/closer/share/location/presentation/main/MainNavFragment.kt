package com.harian.closer.share.location.presentation.main

import androidx.navigation.fragment.findNavController
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.utils.extension.Animation
import com.harian.closer.share.location.utils.extension.navigateWithAnimation
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentMainNavBinding

class MainNavFragment : BaseFragment<FragmentMainNavBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_main_nav

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            floatingActionButton.setOnClickListener {
                findNavController().navigateWithAnimation(
                    MainNavFragmentDirections.actionMainNavFragmentToCreatePostFragment(),
                    Animation.SlideUp
                )
            }
        }
    }
}

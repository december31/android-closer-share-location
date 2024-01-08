package com.harian.closer.share.location.presentation.main

import android.annotation.SuppressLint
import android.os.Build
import android.view.WindowInsets
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.findNavController
import com.harian.closer.share.location.platform.AppManager
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.utils.extension.Animation
import com.harian.closer.share.location.utils.extension.findHomeNavController
import com.harian.closer.share.location.utils.extension.isOnBackStack
import com.harian.closer.share.location.utils.extension.navigateWithAnimation
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentMainNavBinding
import javax.inject.Inject
import kotlin.system.exitProcess

class MainNavFragment : BaseFragment<FragmentMainNavBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_main_nav

    @Inject
    lateinit var appManager: AppManager

    @SuppressLint("WrongConstant")
    override fun setupSystemBarBehavior() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val insets = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.statusBars())
                binding.root.setPadding(0, 0, 0, insets.bottom)
            }
            WindowInsetsCompat.CONSUMED
        }
    }

    override fun setupUI() {
        super.setupUI()
        handleOnBackPressed()
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            floatingActionButton.setOnClickListener {
                findNavController().navigateWithAnimation(
                    MainNavFragmentDirections.actionMainNavFragmentToCreatePostFragment(),
                    Animation.SlideUp
                )
            }

            icHome.setOnClickListener {
                openOrPopIfExisted(R.id.home_fragment)
            }
            icMap.setOnClickListener {
                openOrPopIfExisted(R.id.map_fragment)
            }
        }
    }

    private fun openOrPopIfExisted(resId: Int) {
        if (findHomeNavController()?.currentDestination?.id == resId) return
        if (findHomeNavController()?.isOnBackStack(resId) == true) {
            findHomeNavController()?.popBackStack(resId, false)
        } else {
            findHomeNavController()?.navigateWithAnimation(resId, Animation.FadeIn)
        }
        updateUIBottomNav()
    }

    private fun updateUIBottomNav() {
        binding.apply {
            icHome.setImageResource(R.drawable.ic_home)
            icMap.setImageResource(R.drawable.ic_map)
            icNotification.setImageResource(R.drawable.ic_notification)
            icProfile.setImageResource(R.drawable.ic_profile)
            when (findHomeNavController()?.currentDestination?.id) {
                R.id.home_fragment -> icHome.setImageResource(R.drawable.ic_home_selected)
                R.id.map_fragment -> icMap.setImageResource(R.drawable.ic_map_seleted)
            }
        }
    }

    private fun handleOnBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (appManager.isBackPressFinish) {
                    activity?.finishAffinity()
                    exitProcess(0)
                } else {
                    showToast("Press back again to quit")
                }
            }
        })
    }
}

package com.harian.closer.share.location.presentation.homenav

import android.annotation.SuppressLint
import android.os.Build
import android.view.WindowInsets
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import com.harian.closer.share.location.platform.AppManager
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.presentation.homenav.home.HomeFragmentDirections
import com.harian.closer.share.location.utils.extension.Animation
import com.harian.closer.share.location.utils.extension.findHomeNavController
import com.harian.closer.share.location.utils.extension.navigateWithAnimation
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentHomeNavBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.system.exitProcess

@AndroidEntryPoint
class HomeNavFragment : BaseFragment<FragmentHomeNavBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_home_nav

    @Inject
    lateinit var appManager: AppManager

    private val sharedViewModel by activityViewModels<MainNavSharedViewModel>()

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
                sharedViewModel.performCenterActionButtonClick()
            }

            icHome.setOnClickListener {
                findHomeNavController()?.navigateWithAnimation(HomeFragmentDirections.openHomeFragment(), Animation.FadeIn)
            }
            icMap.setOnClickListener {
                findHomeNavController()?.navigateWithAnimation(HomeFragmentDirections.openMapFragment(), Animation.FadeIn)
            }
            icNotification.setOnClickListener {
                findHomeNavController()?.navigateWithAnimation(HomeFragmentDirections.openNotificationFragment(), Animation.FadeIn)
            }
            icProfile.setOnClickListener {
                findHomeNavController()?.navigateWithAnimation(HomeFragmentDirections.openProfileFragment(), Animation.FadeIn)
            }

            findHomeNavController()?.addOnDestinationChangedListener { _, destination, _ ->
                icHome.setImageResource(R.drawable.ic_home)
                icMap.setImageResource(R.drawable.ic_map)
                icNotification.setImageResource(R.drawable.ic_notification)
                icProfile.setImageResource(R.drawable.ic_profile)
                when (destination.id) {
                    R.id.home_fragment -> {
                        icHome.setImageResource(R.drawable.ic_home_selected)
                        floatingActionButton.setImageResource(R.drawable.ic_add)
                    }

                    R.id.map_fragment -> {
                        icMap.setImageResource(R.drawable.ic_map_seleted)
                        floatingActionButton.setImageResource(R.drawable.ic_my_location)
                    }

                    R.id.notification_fragment -> {
                        icNotification.setImageResource(R.drawable.ic_notification_selected)
                        floatingActionButton.setImageResource(R.drawable.ic_add)
                    }

                    R.id.profile_fragment -> {
                        icProfile.setImageResource(R.drawable.ic_profile_selected)
                        floatingActionButton.setImageResource(R.drawable.ic_add)
                    }
                }
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

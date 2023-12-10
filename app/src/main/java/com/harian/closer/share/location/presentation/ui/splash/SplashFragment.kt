package com.harian.closer.share.location.presentation.ui.splash

import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.harian.closer.share.location.base.BaseFragment
import com.harian.closer.share.location.R
import com.harian.closer.share.location.databinding.FragmentSplashBinding
import com.harian.closer.share.location.utils.extension.navigateWithAnimation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * user for login process when user start using app
 */
class SplashFragment : BaseFragment<FragmentSplashBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_splash

    override fun setupUI() {
        super.setupUI()
        lifecycleScope.launch {
            delay(5000)
            withContext(Dispatchers.Main) {
                findNavController().navigateWithAnimation(SplashFragmentDirections.actionSplashFragmentToNavHome())
            }
        }
    }
}

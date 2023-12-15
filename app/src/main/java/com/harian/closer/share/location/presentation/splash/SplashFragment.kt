package com.harian.closer.share.location.presentation.splash

import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.harian.closer.share.location.R
import com.harian.closer.share.location.databinding.FragmentSplashBinding
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.presentation.login.LoginViewModel
import com.harian.closer.share.location.utils.extension.navigateWithAnimation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    private val loginViewModel by viewModels<LoginViewModel>()

    override val layoutId: Int
        get() = R.layout.fragment_splash

    override fun setupUI() {
        super.setupUI()
        lifecycleScope.launch {
            delay(5000)
            findNavController().navigateWithAnimation(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
        }
    }
}

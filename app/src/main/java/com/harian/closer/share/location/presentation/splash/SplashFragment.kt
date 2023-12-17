package com.harian.closer.share.location.presentation.splash

import android.annotation.SuppressLint
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.harian.closer.share.location.platform.AppManager
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.presentation.login.LoginViewModel
import com.harian.closer.share.location.utils.extension.navigateWithAnimation
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.system.exitProcess

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    private val loginViewModel by viewModels<LoginViewModel>()

    @Inject
    lateinit var appManager: AppManager

    override val layoutId: Int
        get() = R.layout.fragment_splash

    override fun setupUI() {
        super.setupUI()
        handleOnBackPressed()
        handleStateChanges()
        login()
    }

    @SuppressLint("SetTextI18n")
    private fun login() {
//        lifecycleScope.launch {
//            repeat(100) {
//                delay(300)
//                withContext(Dispatchers.Main) {
//                    var dots = ""
//                    for (i in 0..(it % 3)) {
//                        dots = "$dots ."
//                        binding.tvLoggingIn.text = context?.getString(R.string.logging_you_in) + dots
//                    }
//                    binding.tvLoggingIn.text = context?.getString(R.string.logging_you_in) + dots
//                }
//            }
//        }
        loginViewModel.refreshToken()
    }

    private fun handleStateChanges() {
        loginViewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                when (state) {
                    is LoginViewModel.FunctionState.Init -> Unit
                    is LoginViewModel.FunctionState.ErrorRefreshToken -> {
                        findNavController().navigateWithAnimation(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
                    }

                    is LoginViewModel.FunctionState.SuccessRefreshToken -> {
                        findNavController().navigateWithAnimation(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
                    }

                    else -> Unit
                }
            }
            .launchIn(lifecycleScope)
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

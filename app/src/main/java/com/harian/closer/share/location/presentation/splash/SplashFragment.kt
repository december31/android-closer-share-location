package com.harian.closer.share.location.presentation.splash

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.harian.closer.share.location.platform.AppManager
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.utils.extension.findGlobalNavController
import com.harian.closer.share.location.utils.extension.navigateWithAnimation
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.system.exitProcess

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    @Inject
    lateinit var appManager: AppManager

    private val viewModel: SplashViewModel by viewModels()

    override val layoutId: Int
        get() = R.layout.fragment_splash

    override fun setupUI() {
        super.setupUI()
        handleOnBackPressed()

        viewModel.verifyToken()
    }

    override fun handleStateChanges() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                when (state) {
                    SplashViewModel.FunctionState.Init -> Unit
                    SplashViewModel.FunctionState.ErrorVerifyToken -> {
                        findGlobalNavController()?.navigateWithAnimation(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
                    }

                    SplashViewModel.FunctionState.SuccessVerifyToken -> {
                        findGlobalNavController()?.navigateWithAnimation(SplashFragmentDirections.actionSplashFragmentToHomeNavFragment())
                    }

                    SplashViewModel.FunctionState.NeedResetPassword -> {
                        findGlobalNavController()?.navigateWithAnimation(
                            SplashFragmentDirections.actionSplashFragmentToLoginFragment(
                                true
                            )
                        )
                    }
                }
            }
            .launchIn(lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.resetState()
    }

    private fun handleOnBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = if (appManager.isBackPressFinish) {
                activity?.finishAffinity()
                exitProcess(0)
            } else {
                showToast(R.string.press_back_again_to_quit)
            }
        })
    }
}

package com.harian.closer.share.location.presentation.flowregister.phonenumber

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.utils.extension.Animation
import com.harian.closer.share.location.utils.extension.findGlobalNavController
import com.harian.closer.share.location.utils.extension.navigateWithAnimation
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentRequestPhoneNumberBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class RequestPhoneNumberFragment : BaseFragment<FragmentRequestPhoneNumberBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_request_phone_number

    private val viewModel: RequestPhoneNumberViewModel by viewModels()

    override fun setupUI() {
        super.setupUI()
        handleStateChanges()
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            btnSkip.setOnClickListener {
                findGlobalNavController()?.navigateWithAnimation(
                    RequestPhoneNumberFragmentDirections.actionRequestPhoneNumberFragmentToHomeNavFragment(),
                    Animation.SlideLeft
                )
            }

            btnUploadPhoneNumber.setOnClickListener {
                if (edtPhoneNumber.text.isNullOrBlank().not()) {
                    viewModel.updateUserPhoneNumber(edtPhoneNumber.text.toString())
                }
            }
        }
    }

    private fun handleStateChanges() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is RequestPhoneNumberViewModel.RequestPhoneNumberState.Init -> Unit
                is RequestPhoneNumberViewModel.RequestPhoneNumberState.Error -> handleError()
                is RequestPhoneNumberViewModel.RequestPhoneNumberState.Loading -> handleLoading(it.isLoading)
                is RequestPhoneNumberViewModel.RequestPhoneNumberState.Success -> handleSuccess()
            }
        }.launchIn(lifecycleScope)
    }

    private fun handleError() {
        showToast(R.string.failed_to_update_phone_number_please_try_again_later)
    }

    private fun handleLoading(isLoading: Boolean) {
        binding.apply {
            loadingAnimation.isVisible = isLoading
            btnUploadPhoneNumber.setTextColor(ContextCompat.getColor(root.context, if (isLoading) R.color.transparent else R.color.white))
            btnUploadPhoneNumber.setIconTintResource(if (isLoading) R.color.transparent else R.color.white)
        }
    }

    private fun handleSuccess() {
        findGlobalNavController()?.navigateWithAnimation(
            RequestPhoneNumberFragmentDirections.actionRequestPhoneNumberFragmentToHomeNavFragment(),
            Animation.SlideLeft
        )
    }
}

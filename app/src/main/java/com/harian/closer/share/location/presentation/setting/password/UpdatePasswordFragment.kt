package com.harian.closer.share.location.presentation.setting.password

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.harian.closer.share.location.data.common.BaseViewModel
import com.harian.closer.share.location.data.user.remote.dto.UpdatePasswordRequest
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentUpdatePasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class UpdatePasswordFragment : BaseFragment<FragmentUpdatePasswordBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_update_password

    private val viewModel: UpdatePasswordViewModel by viewModels()

    override fun getFragmentViewModel(): BaseViewModel {
        return viewModel
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            icBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnUpdatePassword.setOnClickListener {
                updatePassword()
            }
        }
    }

    override fun handleStateChanges() {
        super.handleStateChanges()
        viewModel.updatePasswordState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach { state ->
            when (state) {
                UpdatePasswordViewModel.UpdatePasswordState.Init -> Unit
                UpdatePasswordViewModel.UpdatePasswordState.Error -> handleErrorUpdatePassword()
                UpdatePasswordViewModel.UpdatePasswordState.Success -> handleSuccessUpdatePassword()
            }
        }.launchIn(lifecycleScope)
    }

    private fun handleErrorUpdatePassword() {
        showToast(R.string.some_thing_went_wrong_please_try_again_later)
    }

    private fun handleSuccessUpdatePassword() {
        showToast(getString(R.string.success_update_password))
        findNavController().popBackStack()
    }

    private fun updatePassword() {
        validateInput { currentPassword, newPassword ->
            if (binding.edtConfirmPassword.text.toString() == newPassword) {
                viewModel.updatePassword(UpdatePasswordRequest(currentPassword, newPassword))
            } else {
                showToast(getString(R.string.confirmation_password_is_incorrect))
            }
        }
    }


    private fun validateInput(onAccepted: (currentPassword: String, newPassword: String) -> Unit) {
        if (isPasswordValid()) {
            onAccepted.invoke(
                binding.edtCurrentPassword.text.toString(),
                binding.edtNewPassword.text.toString()
            )
        } else {
            showToast(getString(R.string.password_invalid_warning))
        }
    }


    private fun isPasswordValid(): Boolean {
        return binding.edtCurrentPassword.text.toString().trim().length >= 8 &&
                binding.edtNewPassword.text.toString().trim().length >= 8
    }
}

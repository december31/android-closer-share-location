package com.harian.closer.share.location.presentation.setting.username

import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentUpdateUsernameBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class UpdateUsernameFragment : BaseFragment<FragmentUpdateUsernameBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_update_username

    private val viewModel by viewModels<UpdateUsernameViewModel>()

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            icBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnUpdateUsername.setOnClickListener {
                if(edtName.text.isNullOrEmpty()) return@setOnClickListener

                viewModel.updateUsername(edtName.text.toString())
            }

            edtName.addTextChangedListener {
                btnUpdateUsername.isEnabled = !it.isNullOrEmpty()
            }
        }
    }

    override fun handleStateChanges() {
        super.handleStateChanges()
        viewModel.updateUsernameState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                UpdateUsernameViewModel.UpdateUsernameState.Init -> Unit
                is UpdateUsernameViewModel.UpdateUsernameState.Error -> showToast(R.string.some_thing_went_wrong_please_try_again_later)
                is UpdateUsernameViewModel.UpdateUsernameState.Success -> handleSuccessUpdateUsername()
            }
        }.launchIn(lifecycleScope)

        viewModel.getUserInformationState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                UpdateUsernameViewModel.GetUserInformationState.Init -> Unit
                is UpdateUsernameViewModel.GetUserInformationState.Error -> handleErrorGetUserInformation()
                is UpdateUsernameViewModel.GetUserInformationState.Success -> handleSuccessGetUserInformation(it.data)
            }
        }.launchIn(lifecycleScope)

        viewModel.fetchUserInformation()
    }

    private fun handleSuccessGetUserInformation(userEntity: UserEntity) {
        binding.edtName.setText(userEntity.name)
    }

    private fun handleErrorGetUserInformation() {
        showToast(R.string.some_thing_went_wrong_please_try_again_later)
        findNavController().popBackStack()
    }

    private fun handleSuccessUpdateUsername() {
        showToast(getString(R.string.success_update_username))
        findNavController().popBackStack()
    }
}

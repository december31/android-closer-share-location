package com.harian.closer.share.location.presentation.addfriend.myqrcode

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.platform.SharedPrefs
import com.harian.closer.share.location.utils.extension.findGlobalNavController
import com.harian.closer.share.location.utils.extension.glideLoadImage
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentMyQrCodeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MyQrCodeFragment : BaseFragment<FragmentMyQrCodeBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_my_qr_code

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private val viewModel: MyQrCodeViewModel by viewModels()

    override fun setupUI() {
        super.setupUI()
        binding.apply {
            btnScan.isSelected = true
            btnShare.isSelected = true
        }
        viewModel.fetchUserInformation()
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            btnBack.setOnClickListener {
                findGlobalNavController()?.popBackStack()
            }
            btnScan.setOnClickListener {
                findGlobalNavController()?.popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.resetStates()
    }

    override fun handleStateChanges() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is MyQrCodeViewModel.GetUserInformationState.Init -> Unit
                is MyQrCodeViewModel.GetUserInformationState.Error -> showToast(it.message)
                is MyQrCodeViewModel.GetUserInformationState.Loading -> binding.frLoading.isVisible = it.isLoading
                is MyQrCodeViewModel.GetUserInformationState.Success -> handleSuccessGetProfileInfo(it.user)
            }
        }.launchIn(lifecycleScope)

        viewModel.generateQrCodeState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is MyQrCodeViewModel.GenerateQrCodeState.Init -> Unit
                is MyQrCodeViewModel.GenerateQrCodeState.Loading -> binding.loadingQrCode.isVisible = it.isLoading
                is MyQrCodeViewModel.GenerateQrCodeState.Success -> binding.imgQrCode.glideLoadImage(it.qrCode)
            }
        }.launchIn(lifecycleScope)
    }

    private fun handleSuccessGetProfileInfo(user: UserEntity) {
        binding.apply {
            tvName.text = user.name
            imgAvatar.glideLoadImage(user.getAuthorizedAvatarUrl(sharedPrefs.getToken()))
        }
    }
}

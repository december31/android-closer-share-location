package com.harian.closer.share.location.presentation.flowregister.avatar

import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.platform.SharedPrefs
import com.harian.closer.share.location.presentation.flowregister.phonenumber.RequestPhoneNumberFragmentDirections
import com.harian.closer.share.location.utils.FileUtils
import com.harian.closer.share.location.utils.clearCache
import com.harian.closer.share.location.utils.extension.Animation
import com.harian.closer.share.location.utils.extension.findGlobalNavController
import com.harian.closer.share.location.utils.extension.glideLoadImage
import com.harian.closer.share.location.utils.extension.navigateWithAnimation
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentRequestAvatarBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class RequestAvatarFragment : BaseFragment<FragmentRequestAvatarBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_request_avatar

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private val viewModel: RequestAvatarViewModel by viewModels()

    private var selectedUri: Uri? = null

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        selectedUri = uri
        if (selectedUri != null) {
            binding.imgAvatar.glideLoadImage(uri)
        }
        binding.btnPickImageBig.isVisible = selectedUri == null
    }

    override fun setupUI() {
        super.setupUI()
        handleStateChanges()
        binding.btnPickImage.isSelected = true
        binding.btnUpload.isSelected = true
        viewModel.getUserInformation()
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            btnPickImage.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            btnUpload.setOnClickListener {
                selectedUri?.let { uri ->
                    viewModel.updateAvatar(FileUtils.getFile(it.context, uri))
                }
            }
            btnPickImageBig.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            btnSkip.setOnClickListener {
                findGlobalNavController()?.navigateWithAnimation(
                    RequestPhoneNumberFragmentDirections.actionRequestPhoneNumberFragmentToHomeNavFragment(),
                    Animation.SlideLeft
                )
            }
        }
    }

    private fun handleStateChanges() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is RequestAvatarViewModel.RequestAvatarState.Init -> Unit
                is RequestAvatarViewModel.RequestAvatarState.FailedGetUserInformation -> handleFailedGetUserInformation()
                is RequestAvatarViewModel.RequestAvatarState.FailedUpdateAvatar -> handleFailedUpdateAvatar()
                is RequestAvatarViewModel.RequestAvatarState.Loading -> handleLoading(it.isLoading)
                is RequestAvatarViewModel.RequestAvatarState.SuccessGetUserInformation -> handleSuccessGetUserInformation(it.data)
                is RequestAvatarViewModel.RequestAvatarState.SuccessUpdateAvatar -> handleSuccessUpdateAvatar()
            }
        }.launchIn(lifecycleScope)
    }

    private fun handleFailedGetUserInformation() {

    }

    private fun handleFailedUpdateAvatar() {
        showToast(R.string.some_thing_went_wrong_please_try_again_later)
    }

    private fun handleLoading(isLoading: Boolean) {
        binding.apply {
            loadingAnimation.isVisible = isLoading
            btnPickImage.isVisible = !isLoading
            separator.isVisible = !isLoading
            btnUpload.setTextColor(ContextCompat.getColor(root.context, if (isLoading) R.color.transparent else R.color.white))
            btnUpload.setIconTintResource(if (isLoading) R.color.transparent else R.color.white)
        }
    }

    private fun handleSuccessGetUserInformation(data: UserEntity) {
        binding.imgAvatar.glideLoadImage(data.getAuthorizedAvatarUrl(sharedPrefs.getToken()))
    }

    private fun handleSuccessUpdateAvatar() {
        findGlobalNavController()?.navigateWithAnimation(
            RequestAvatarFragmentDirections.actionRequestAvatarFragmentToRequestPhoneNumberFragment(),
            Animation.SlideLeft
        )
        context?.clearCache()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        selectedUri = null
    }
}

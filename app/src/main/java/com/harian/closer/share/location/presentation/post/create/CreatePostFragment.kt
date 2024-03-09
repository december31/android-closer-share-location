package com.harian.closer.share.location.presentation.post.create

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.harian.closer.share.location.data.post.remote.dto.PostRequest
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.presentation.common.UserViewModel
import com.harian.closer.share.location.utils.FileUtils
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentCreatePostBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class CreatePostFragment : BaseFragment<FragmentCreatePostBinding>() {

    private val userViewModel: UserViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()

    private var selectedImages = arrayListOf<Uri>()

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        selectedImages.addAll(uris)
        val images = arrayListOf<Uri>()
        images.addAll(selectedImages)
        selectedImages.clear()
        selectedImages.addAll(images.distinct())

        selectedImages.forEach { uri ->
            Log.d(this@CreatePostFragment.javaClass.simpleName, uri.path.toString())
        }

        updateUiImages()
    }

    override val layoutId: Int
        get() = R.layout.fragment_create_post

    @SuppressLint("WrongConstant")
    override fun setupSystemBarBehavior() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val insets = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.statusBars())
                (binding.toolbar.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = insets.top

                val btnAddPhotoLayoutParam = binding.btnAddPhoto.layoutParams as? ViewGroup.MarginLayoutParams
                btnAddPhotoLayoutParam?.bottomMargin = insets.bottom + (btnAddPhotoLayoutParam?.bottomMargin ?: 0)
            }
            WindowInsetsCompat.CONSUMED
        }
    }

    override fun setupUI() {
        super.setupUI()
        handleStateChanges()
        userViewModel.fetchUserInformation()
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            binding.icClose.setOnClickListener {
                findNavController().popBackStack()
            }
            btnPost.setOnClickListener {
                createPost()
            }
            btnAddPhoto.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }


    private fun handleStateChanges() {
        userViewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is UserViewModel.FunctionState.Init -> Unit
                is UserViewModel.FunctionState.IsLoading -> Unit
                is UserViewModel.FunctionState.ErrorGetUserInfo -> Unit
                is UserViewModel.FunctionState.SuccessGetUserInfo -> {
                    context?.let { ctx ->
                        val user = it.userEntity
                        Glide.with(ctx).load(user.getAuthorizedAvatarUrl(postViewModel.sharedPrefs.getToken()))
                            .into(binding.imgAvatar)

                        binding.tvUsername.text = it.userEntity.name
                    }
                }
            }
        }.launchIn(lifecycleScope)

        postViewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is PostViewModel.FunctionState.Init -> Unit
                is PostViewModel.FunctionState.IsLoading -> handleIsLoading(it.isLoading)
                is PostViewModel.FunctionState.ErrorCreatePost -> handleErrorCreatePost()
                is PostViewModel.FunctionState.SuccessCreatePost -> handleSuccessCreatePost()
            }
        }.launchIn(lifecycleScope)
    }

    private fun handleSuccessCreatePost() {
        showToast(getString(R.string.posted_successful))
        findNavController().popBackStack()
    }

    private fun handleErrorCreatePost() {
        showToast(getString(R.string.failed_to_create_post_please_try_again_later))
    }

    private fun handleIsLoading(isLoading: Boolean) {
        binding.loadingContainer.isVisible = isLoading
        if (isLoading) {
            binding.loadingAnimation.playAnimation()
        } else {
            binding.loadingAnimation.cancelAnimation()
        }
    }

    private fun updateUiImages() {
        binding.multipleImagesView.isVisible = selectedImages.isNotEmpty()
        binding.multipleImagesView.loadImages(selectedImages)
    }

    private fun createPost() {
        if (!binding.edtTitle.text.isNullOrBlank() || !binding.edtContent.text.isNullOrBlank()) {
            val images = context?.let { ctx ->
                selectedImages.map { uri ->
                    FileUtils.getFile(ctx, uri)
                }
            }
            postViewModel.createPost(
                PostRequest(binding.edtTitle.text.toString(), binding.edtContent.text.toString()),
                images
            )
        } else {
            showToast(getString(R.string.please_enter_at_least_title_or_description))
        }
    }
}

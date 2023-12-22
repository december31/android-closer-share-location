package com.harian.closer.share.location.presentation.post.create

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.harian.closer.share.location.data.post.remote.dto.CreatePostRequest
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.presentation.common.UserViewModel
import com.harian.closer.share.location.utils.Constants
import com.harian.closer.share.location.utils.extension.FileUtils
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentCreatePostBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


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
                        val avatarUrl =
                            user.avatar ?: Constants.DEFAULT_IMAGE_URL
                        Glide.with(ctx).load(avatarUrl).into(binding.imgAvatar)

                        binding.tvUsername.text = it.userEntity.name
                    }
                }
            }
        }.launchIn(lifecycleScope)

        postViewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is PostViewModel.FunctionState.Init -> Unit
                is PostViewModel.FunctionState.IsLoading -> handleIsLoading(it.isLoading, R.raw.loading_photo)
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

    private fun handleIsLoading(isLoading: Boolean, rawRes: Int? = null) {
        binding.loadingContainer.isVisible = isLoading
        if (isLoading) {
            rawRes?.let { binding.loadingAnimation.setAnimation(it) }
            binding.loadingAnimation.playAnimation()
        } else {
            binding.loadingAnimation.cancelAnimation()
        }
    }

    private fun updateUiImages() {
        handleIsLoading(true, R.raw.loading_photo)
        binding.imagesContainer.imagesCount = selectedImages.size
        binding.imagesContainer.remainImagesCount.text = getString(R.string.remain_image_count, selectedImages.size - 5)
        lifecycleScope.launch {
            delay(1000)
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val imageIterator = selectedImages.iterator()
                binding.imagesContainer.container.children.forEach { view ->
                    if (view is ImageView) {
                        if (view.isVisible) {
                            context?.let {
                                if (imageIterator.hasNext()) {
                                    Glide.with(it).load(imageIterator.next()).into(view)
                                }
                            }
                        }
                    }
                }
                handleIsLoading(false)
            }
        }
    }

    private fun createPost() {
        if (binding.edtTitle.text.isNullOrBlank() && binding.edtTitle.text.isNullOrBlank()) {
            val images = context?.let { ctx ->
                selectedImages.map { uri ->
                    FileUtils.getFile(ctx, uri)
                }
            }
            postViewModel.createPost(
                CreatePostRequest(binding.edtTitle.text.toString(), binding.edtContent.text.toString()),
                images
            )
        } else {
            showToast(getString(R.string.please_enter_at_least_title_or_description))
        }
    }
}

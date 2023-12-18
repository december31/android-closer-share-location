package com.harian.closer.share.location.presentation.post.create

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.harian.closer.share.location.data.post.remote.dto.CreatePostRequest
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.presentation.common.UserViewModel
import com.harian.closer.share.location.utils.Constants
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentCreatePostBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class CreatePostFragment : BaseFragment<FragmentCreatePostBinding>() {

    private val userViewModel: UserViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()

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

    private fun createPost() {
        postViewModel.createPost(
            CreatePostRequest("", binding.edtContent.text.toString())
        )
    }
}

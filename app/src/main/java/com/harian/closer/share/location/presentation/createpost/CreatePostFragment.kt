package com.harian.closer.share.location.presentation.createpost

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
    }

    private fun createPost() {
        postViewModel.createPost(
            CreatePostRequest("", binding.edtContent.text.toString())
        )
    }
}

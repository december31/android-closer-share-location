package com.harian.closer.share.location.presentation.post.details

import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.harian.closer.share.location.data.post.remote.dto.CommentRequest
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.platform.SharedPrefs
import com.harian.closer.share.location.utils.Constants
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentPostDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class PostDetailsFragment : BaseFragment<FragmentPostDetailsBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_post_details

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private val viewModel by viewModels<PostDetailsViewModel>()
    private val safeArgs by navArgs<PostDetailsFragmentArgs>()
    private lateinit var adapter: PostDetailsAdapter
    override fun setupUI() {
        super.setupUI()
        activity?.window?.let { WindowCompat.setDecorFitsSystemWindows(it, true) }
        setupRecyclerView()
        handleStateChanges()
        fetchData()
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            icBack.setOnClickListener {
                findNavController().popBackStack()
            }
            btnPostComment.setOnClickListener {
                if (!edtComment.text.isNullOrBlank()) {
                    viewModel.createComment(CommentRequest(edtComment.text.toString()), safeArgs.postId)
                } else {
                    edtComment.performClick()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = PostDetailsAdapter(sharedPrefs.getToken())
        binding.rvDetailsPost.adapter = adapter
    }

    private fun fetchData() {
        if (safeArgs.postId > 0) {
            viewModel.fetchPostData(safeArgs.postId)
            viewModel.fetchUserData()
        } else {
            handleErrorGetPost()
        }
    }

    private fun handleStateChanges() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is PostDetailsViewModel.FunctionState.Init -> Unit
                is PostDetailsViewModel.FunctionState.ErrorGetUserUserInfo -> Unit
                is PostDetailsViewModel.FunctionState.IsLoading -> handleIsLoading(it.isLoading)
                is PostDetailsViewModel.FunctionState.ErrorGetPost -> handleErrorGetPost()
                is PostDetailsViewModel.FunctionState.SuccessGetPost -> handleSuccessGetPost(it.postDataList)
                is PostDetailsViewModel.FunctionState.SuccessGetUserUserInfo -> handleSuccessGetUserInfo(it.userEntity)
                is PostDetailsViewModel.FunctionState.ErrorCreateComment -> handleErrorCreateComment()
                is PostDetailsViewModel.FunctionState.SuccessCreateComment -> handleSuccessCrateComment()
            }
        }.launchIn(lifecycleScope)
    }

    private fun handleErrorCreateComment() {
        showToast(getString(R.string.post_comment_failed_please_try_again_later))
    }

    private fun handleSuccessCrateComment() {
        viewModel.fetchPostData(safeArgs.postId)
    }

    private fun handleIsLoading(isLoading: Boolean) {
        binding.loadingContainer.isVisible = isLoading
        if (isLoading) {
            binding.loadingAnimation.playAnimation()
        } else {
            binding.loadingAnimation.cancelAnimation()
        }
    }

    private fun handleSuccessGetUserInfo(user: UserEntity) {
        Glide.with(binding.root).load(user.avatar ?: Constants.DEFAULT_IMAGE_URL).into(binding.imgUserAvatar)
    }

    private fun handleSuccessGetPost(postDataList: List<Any>) {
        adapter.updateData(postDataList)
        binding.rvDetailsPost.layoutManager?.smoothScrollToPosition(binding.rvDetailsPost, RecyclerView.State(), 0)
    }

    private fun handleErrorGetPost() {
        showToast(getString(R.string.this_post_is_no_longer_existed))
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.window?.let { WindowCompat.setDecorFitsSystemWindows(it, false) }
    }
}

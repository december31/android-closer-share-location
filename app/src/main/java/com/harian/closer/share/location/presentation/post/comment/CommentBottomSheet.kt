package com.harian.closer.share.location.presentation.post.comment

import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.harian.closer.share.location.data.post.remote.dto.CommentRequest
import com.harian.closer.share.location.domain.comment.entity.CommentEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.platform.BaseBottomSheetDialogFragment
import com.harian.closer.share.location.platform.SharedPrefs
import com.harian.closer.share.location.utils.extension.glideLoadImage
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.BottomSheetCommentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class CommentBottomSheet : BaseBottomSheetDialogFragment<BottomSheetCommentBinding>() {
    override val layoutId: Int
        get() = R.layout.bottom_sheet_comment

    @Inject
    lateinit var adapter: CommentAdapter

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private val viewModel by viewModels<CommentViewModel>()

    companion object {
        private const val KEY_POST_ID = "KEY_POST_ID"
        fun newInstance(postId: Int) = CommentBottomSheet().apply {
            arguments = bundleOf(KEY_POST_ID to postId)
        }
    }

    override fun setupUI() {
        super.setupUI()
        handleStateChanges()
        val postId = getPostId() ?: return
        setupRecyclerView()
        viewModel.fetchPostComments(postId)
        viewModel.fetchUserData()
    }

    private fun setupRecyclerView() {
        binding.apply {
            rvComments.adapter = adapter
        }
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            btnPostComment.setOnClickListener {
                if (!edtComment.text.isNullOrBlank()) {
                    viewModel.createComment(CommentRequest(edtComment.text.toString()))
                    edtComment.text.clear()
                } else {
                    edtComment.performClick()
                }
            }
        }
    }

    private fun handleStateChanges() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is CommentViewModel.State.Init -> Unit
                is CommentViewModel.State.ErrorCreateComment -> handleErrorCreateComment()
                is CommentViewModel.State.ErrorGetComments -> handleErrorGetComments()
                is CommentViewModel.State.ErrorGetUserUserInfo -> handleErrorGetUserUserInfo()
                is CommentViewModel.State.IsLoading -> handleLoading(it.isLoading)
                is CommentViewModel.State.SuccessCreateComment -> handleSuccessCreateComment()
                is CommentViewModel.State.SuccessGetComments -> handleSuccessGetComments(it.comments)
                is CommentViewModel.State.SuccessGetUserUserInfo -> handleSuccessGetUserUserInfo(it.user)
            }
        }.launchIn(lifecycleScope)
    }

    private fun handleErrorCreateComment() {
        showToast("Failed to create comment, please try again later!")
    }

    private fun handleErrorGetComments() {
        showToast("Failed to get comments, please try again later!")
    }

    private fun handleErrorGetUserUserInfo() {
        showToast("Failed to get user info, please try again later!")
    }

    private fun handleLoading(isLoading: Boolean) {
        binding.loadingContainer.isVisible = isLoading
    }

    private fun handleSuccessCreateComment() {
        showToast("Comment created successfully!")
    }

    private fun handleSuccessGetComments(comments: List<CommentEntity>) {
        adapter.updateData(comments)
        binding.apply {
            noCommentAnimation.isVisible = comments.isEmpty()
            tvNoComment.isVisible = comments.isEmpty()
            rvComments.smoothScrollToPosition(0)
        }
    }

    private fun handleSuccessGetUserUserInfo(user: UserEntity) {
        binding.imgUserAvatar.glideLoadImage(user.getAuthorizedAvatarUrl(sharedPrefs.getToken()))
    }

    private fun getPostId(): Int? {
        val postId = if (arguments != null) {
            arguments?.getInt(KEY_POST_ID)
        } else {
            showToast("An error occurred, please try again later!")
            null
        }
        if (postId == null) {
            dismiss()
        }
        return postId
    }
}

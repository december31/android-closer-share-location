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
import com.harian.closer.share.location.domain.post.entity.ImageEntity
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.presentation.post.comment.CommentBottomSheet
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentPostDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class PostDetailsFragment : BaseFragment<FragmentPostDetailsBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_post_details

    private val viewModel by viewModels<PostDetailsViewModel>()
    private val args by navArgs<PostDetailsFragmentArgs>()
    private lateinit var adapter: PostDetailsAdapter

    override fun setupUI() {
        super.setupUI()
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
        }
    }

    private fun setupRecyclerView() {
        adapter = PostDetailsAdapter(viewModel.sharedPrefs.getToken()).apply {
            setListener(object : PostDetailsAdapter.Listener {
                override fun onClickImage(image: ImageEntity) {
                    viewModel.post?.images?.let {
                        findNavController().navigate(
                            PostDetailsFragmentDirections.actionPostDetailsFragmentToImagesViewerFragment(it.toTypedArray())
                        )
                    }
                }

                override fun onClickComment(postEntity: PostEntity) {
                    CommentBottomSheet.newInstance(args.postId).show(childFragmentManager, null)
                }

                override fun onClickLike(postEntity: PostEntity) {

                }
            })
        }
        binding.rvDetailsPost.adapter = adapter
    }

    private fun fetchData() {
        if (args.postId > 0) {
            viewModel.fetchPostData(args.postId)
        } else {
            handleErrorGetPost()
        }
    }

    private fun handleStateChanges() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is PostDetailsViewModel.FunctionState.Init -> Unit
                is PostDetailsViewModel.FunctionState.IsLoading -> handleIsLoading(it.isLoading)
                is PostDetailsViewModel.FunctionState.ErrorGetPost -> handleErrorGetPost()
                is PostDetailsViewModel.FunctionState.SuccessGetPost -> handleSuccessGetPost(it.postDataList)
            }
        }.launchIn(lifecycleScope)
    }

    private fun handleIsLoading(isLoading: Boolean) {
        binding.loadingContainer.isVisible = isLoading
        if (isLoading) {
            binding.loadingAnimation.playAnimation()
        } else {
            binding.loadingAnimation.cancelAnimation()
        }
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

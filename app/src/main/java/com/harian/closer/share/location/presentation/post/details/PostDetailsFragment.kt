package com.harian.closer.share.location.presentation.post.details

import androidx.core.view.WindowCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.harian.closer.share.location.data.common.BaseViewModel
import com.harian.closer.share.location.domain.post.entity.ImageEntity
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.presentation.homenav.home.HomeViewModel
import com.harian.closer.share.location.presentation.post.comment.CommentBottomSheet
import com.harian.closer.share.location.utils.extension.navigateWithAnimation
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
    private val homeViewModel by viewModels<HomeViewModel>()
    private val args by navArgs<PostDetailsFragmentArgs>()
    private lateinit var adapter: PostDetailsAdapter

    override fun getFragmentViewModel(): BaseViewModel = viewModel

    override fun setupUI() {
        super.setupUI()
        setupRecyclerView()
        fetchData()
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            icBack.setOnClickListener {
                findNavController().popBackStack()
            }

            icLike.setOnClickListener {
                viewModel.likeOrUnlikePost()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = PostDetailsAdapter(viewModel.sharedPrefs.getToken()).apply {
            setListener(object : PostDetailsAdapter.Listener {
                override fun onClickImage(image: ImageEntity) {
                    viewModel.postLiveData.value?.images?.let {
                        findNavController().navigateWithAnimation(
                            PostDetailsFragmentDirections.actionPostDetailsFragmentToImagesViewerFragment(it.toTypedArray())
                        )
                    }
                }

                override fun onClickComment(postEntity: PostEntity) {
                    CommentBottomSheet.newInstance(args.postId).show(childFragmentManager, null)
                }

                override fun onClickLike(postEntity: PostEntity) {
                    homeViewModel.likePost(postEntity)
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

    override fun handleStateChanges() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is PostDetailsViewModel.FetchPostState.Init -> Unit
                is PostDetailsViewModel.FetchPostState.ErrorGetPost -> handleErrorGetPost()
                is PostDetailsViewModel.FetchPostState.SuccessGetPost -> handleSuccessGetPost(it.postDataList)
            }
        }.launchIn(lifecycleScope)

        homeViewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is HomeViewModel.ApiState.ErrorUnlikePost -> showToast("Error Unlike Post")
                is HomeViewModel.ApiState.ErrorLikePost -> showToast("Error Like Post")
                is HomeViewModel.ApiState.SuccessLikePost -> showToast("Success Like Post")
                is HomeViewModel.ApiState.SuccessUnlikePost -> showToast("Success Unlike Post")
                else -> Unit
            }
        }.launchIn(lifecycleScope)

        viewModel.postLiveData.observe(viewLifecycleOwner) {
            binding.icLike.setImageResource(if (it.isLiked) R.drawable.ic_liked else R.drawable.ic_like)
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

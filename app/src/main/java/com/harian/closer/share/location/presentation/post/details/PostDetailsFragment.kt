package com.harian.closer.share.location.presentation.post.details

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.harian.closer.share.location.platform.BaseFragment
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
    private val safeArgs by navArgs<PostDetailsFragmentArgs>()
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
        adapter = PostDetailsAdapter()
        binding.rvDetailsPost.adapter = adapter
    }

    private fun fetchData() {
        if (safeArgs.postId > 0) {
            viewModel.fetchPostData(safeArgs.postId)
        } else {
            handleErrorGetPost()
        }
    }

    private fun handleStateChanges() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is PostDetailsViewModel.FunctionState.Init -> Unit
                is PostDetailsViewModel.FunctionState.ErrorGetPost -> handleErrorGetPost()
                is PostDetailsViewModel.FunctionState.SuccessGetPost -> handleSuccessGetPost(it.postDataList)
            }
        }.launchIn(lifecycleScope)
    }

    private fun handleSuccessGetPost(postDataList: List<Any>) {
        adapter.updateData(postDataList)
    }

    private fun handleErrorGetPost() {
        showToast(getString(R.string.this_post_is_no_longer_existed))
        findNavController().popBackStack()
    }
}

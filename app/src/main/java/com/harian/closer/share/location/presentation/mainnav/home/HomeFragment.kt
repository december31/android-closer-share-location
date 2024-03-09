package com.harian.closer.share.location.presentation.mainnav.home

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.platform.AppManager
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.presentation.mainnav.MainNavFragmentDirections
import com.harian.closer.share.location.utils.extension.findMainNavController
import com.harian.closer.share.location.utils.extension.navigateWithAnimation
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_home

    @Inject
    lateinit var appManager: AppManager

    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var adapter: PostAdapter

    override fun setupUI() {
        super.setupUI()
        setupRecyclerView()
        handleStateChanges()
        viewModel.fetchPopularPosts()
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            swipeRefresh.setOnRefreshListener {
                viewModel.fetchPopularPosts()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = PostAdapter(viewModel.sharedPrefs.getToken()).apply {
            setOnItemClickListener { postId ->
                findMainNavController()?.navigateWithAnimation(
                    MainNavFragmentDirections.actionMainNavFragmentToPostDetailsFragment(postId)
                )
            }
            setOnLikePostListener { post ->
                if (post.isLiked) {
                    viewModel.likePost(post)
                } else {
                    viewModel.unlikePost(post)
                }
            }
        }
        binding.rvPost.adapter = adapter
    }

    private fun handleStateChanges() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is HomeViewModel.FunctionState.Init -> Unit
                is HomeViewModel.FunctionState.ErrorGetPopularPosts -> handleOnErrorFetchPosts()
                is HomeViewModel.FunctionState.SuccessGetPopularPosts -> handleOnSuccessFetchPosts(it.posts)
                is HomeViewModel.FunctionState.SuccessLikePost -> Unit
                is HomeViewModel.FunctionState.ErrorLikePost -> handleOnErrorLikePost(it.post)
                is HomeViewModel.FunctionState.ErrorUnlikePost -> Unit
                is HomeViewModel.FunctionState.SuccessUnlikePost -> Unit
            }
        }.launchIn(lifecycleScope)
    }

    private fun handleOnErrorLikePost(post: PostEntity) {
        lifecycleScope.launch {
            val position = post.id?.let { adapter.getItemPosition(it) }
            withContext(Dispatchers.Main) {
                if (position != null) {
                    adapter.updatePostReactions(position)
                }
            }
        }
    }

    private fun handleOnErrorFetchPosts() {
        binding.swipeRefresh.isRefreshing = false
    }

    private fun handleOnSuccessFetchPosts(posts: List<PostEntity>) {
        adapter.updateData(posts)
        binding.swipeRefresh.isRefreshing = false
        binding.rvPost.layoutManager?.smoothScrollToPosition(binding.rvPost, RecyclerView.State(), 0)
    }
}

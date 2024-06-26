package com.harian.closer.share.location.presentation.homenav.home

import android.Manifest
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.platform.AppManager
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.presentation.homenav.HomeNavFragmentDirections
import com.harian.closer.share.location.presentation.homenav.HomeNavSharedViewModel
import com.harian.closer.share.location.presentation.search.SearchFragment
import com.harian.closer.share.location.utils.extension.Animation
import com.harian.closer.share.location.utils.extension.findGlobalNavController
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

    @Inject
    lateinit var adapter: PostAdapter

    private val viewModel by viewModels<HomeViewModel>()
    private val sharedViewModel by activityViewModels<HomeNavSharedViewModel>()

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    override fun setupUI() {
        super.setupUI()
        setupRecyclerView()

        viewModel.fetchPopularPosts()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            swipeRefresh.setOnRefreshListener {
                viewModel.fetchPopularPosts()
            }

            btnCreateNewPost.setOnClickListener {
                findGlobalNavController()?.navigateWithAnimation(
                    HomeNavFragmentDirections.actionHomeNavFragmentToCreatePostFragment(),
                    Animation.SlideUp
                )
                sharedViewModel.resetCenterActionButtonClick()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter.apply {
            setOnItemClickListener { postId ->
                findGlobalNavController()?.navigateWithAnimation(
                    HomeNavFragmentDirections.actionHomeNavFragmentToPostDetailsFragment(postId)
                )
            }
            setOnLikePostListener { post ->
                if (post.isLiked) {
                    viewModel.likePost(post)
                } else {
                    viewModel.unlikePost(post)
                }
            }
            setOnAvatarClickListener {
                findGlobalNavController()?.navigateWithAnimation(
                    HomeNavFragmentDirections.actionHomeNavFragmentToProfileFragment(it)
                )
            }
        }
        binding.rvPost.adapter = adapter
    }

    override fun handleStateChanges() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is HomeViewModel.ApiState.Init -> Unit
                is HomeViewModel.ApiState.ErrorGetPopularPosts -> handleOnErrorFetchPosts()
                is HomeViewModel.ApiState.SuccessGetPopularPosts -> handleOnSuccessFetchPosts(it.posts)
                is HomeViewModel.ApiState.SuccessLikePost -> Unit
                is HomeViewModel.ApiState.ErrorLikePost -> handleOnErrorLikePost(it.post)
                is HomeViewModel.ApiState.ErrorUnlikePost -> Unit
                is HomeViewModel.ApiState.SuccessUnlikePost -> Unit
            }
        }.launchIn(lifecycleScope)

        sharedViewModel.centerActionButtonClickLiveData.observe(viewLifecycleOwner) {
            if (it) {
                findGlobalNavController()?.navigateWithAnimation(
                    HomeNavFragmentDirections.actionHomeNavFragmentToSearchFragment(SearchFragment.SearchType.POST.name),
                    Animation.SlideUp
                )
                sharedViewModel.resetCenterActionButtonClick()
            }
        }
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
        binding.rvPost.layoutManager?.smoothScrollToPosition(
            binding.rvPost,
            RecyclerView.State(),
            0
        )
    }
}

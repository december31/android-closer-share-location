package com.harian.closer.share.location.presentation.home

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.platform.AppManager
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.platform.SharedPrefs
import com.harian.closer.share.location.utils.extension.navigateWithAnimation
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.system.exitProcess

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_home

    @Inject
    lateinit var appManager: AppManager

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var adapter: PostAdapter

    override fun setupUI() {
        super.setupUI()
        setupRecyclerView()
        handleOnBackPressed()
        handleStateChanges()
        viewModel.fetchPopularPosts()
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            btn.setOnClickListener {
                findNavController().navigateWithAnimation(HomeFragmentDirections.actionHomeFragmentToCreatePostFragment())
            }
            swipeRefresh.setOnRefreshListener {
                viewModel.fetchPopularPosts()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = PostAdapter(sharedPrefs.getToken()).apply {
            setOnItemClickListener { postId ->
                findNavController().navigateWithAnimation(HomeFragmentDirections.actionHomeFragmentToPostDetailsFragment(postId))
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
            }
        }.launchIn(lifecycleScope)
    }

    private fun handleOnErrorFetchPosts() {
        binding.swipeRefresh.isRefreshing = false
    }

    private fun handleOnSuccessFetchPosts(posts: List<PostEntity>) {
        adapter.updateData(posts)
        binding.swipeRefresh.isRefreshing = false
        binding.rvPost.layoutManager?.smoothScrollToPosition(binding.rvPost, RecyclerView.State(), 0)
    }

    private fun handleOnBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (appManager.isBackPressFinish) {
                    activity?.finishAffinity()
                    exitProcess(0)
                } else {
                    showToast("Press back again to quit")
                }
            }
        })
    }
}

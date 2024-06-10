package com.harian.closer.share.location.presentation.search

import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.presentation.homenav.home.PostAdapter
import com.harian.closer.share.location.utils.extension.Animation
import com.harian.closer.share.location.utils.extension.findGlobalNavController
import com.harian.closer.share.location.utils.extension.navigateWithAnimation
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_search

    @Inject
    lateinit var userAdapter: UserAdapter

    @Inject
    lateinit var postAdapter: PostAdapter

    private val viewModel: SearchViewModel by viewModels()
    private val args by navArgs<SearchFragmentArgs>()

    override fun setupUI() {
        super.setupUI()
        setupRecyclerView()

    }

    override fun handleStateChanges() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is SearchViewModel.SearchState.Init -> Unit
                is SearchViewModel.SearchState.Loading -> handleLoading(it.isLoading)
                is SearchViewModel.SearchState.SearchUsersFailed -> Unit
                is SearchViewModel.SearchState.SearchUsersSuccessSuccess -> handleSearchUsersSuccess(it.data)
                is SearchViewModel.SearchState.SearchPostsFailed -> handleSearchPostsFailed()
                is SearchViewModel.SearchState.SearchPostsSuccessSuccess -> handleSearchPostsSuccess(it.data)
            }
        }.launchIn(lifecycleScope)
    }

    private fun handleLoading(loading: Boolean) {
        binding.loading.isVisible = loading
    }

    private fun handleSearchPostsFailed() {

    }

    private fun handleSearchPostsSuccess(data: List<PostEntity>) {
        postAdapter.updateData(data)
    }

    private fun handleSearchUsersSuccess(data: List<UserEntity>) {
        userAdapter.updateData(data)
    }

    private fun setupRecyclerView() {
        userAdapter.apply {
            setOnItemClick {
                findGlobalNavController()?.navigateWithAnimation(
                    SearchFragmentDirections.actionSearchFragmentToProfileFragment(it),
                    Animation.SlideLeft
                )
            }
        }

        postAdapter.apply {
            setOnAvatarClickListener {
                findGlobalNavController()?.navigateWithAnimation(
                    SearchFragmentDirections.actionSearchFragmentToProfileFragment(it),
                    Animation.SlideLeft
                )
            }

            setOnItemClickListener {
                findGlobalNavController()?.navigateWithAnimation(
                    SearchFragmentDirections.actionSearchFragmentToPostDetailsFragment(it),
                    Animation.SlideLeft
                )
            }
        }

        when (SearchType.valueOf(args.searchType)) {
            SearchType.USER -> {
                binding.rvSearchResult.adapter = userAdapter
            }

            SearchType.POST -> {
                binding.rvSearchResult.adapter = postAdapter
            }
        }
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            btnClose.setOnClickListener {
                findGlobalNavController()?.popBackStack()
            }

            edtSearch.addTextChangedListener {
                when (SearchType.valueOf(args.searchType)) {
                    SearchType.USER -> {
                        viewModel.searchUser(it.toString())
                    }
                    SearchType.POST -> {
                        viewModel.searchPost(it.toString())
                    }
                }
            }
        }
    }

    enum class SearchType {
        USER, POST
    }
}

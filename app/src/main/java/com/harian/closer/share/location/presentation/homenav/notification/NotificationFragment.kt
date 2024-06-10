package com.harian.closer.share.location.presentation.homenav.notification

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.harian.closer.share.location.domain.user.entity.FriendRequestEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.presentation.homenav.HomeNavFragmentDirections
import com.harian.closer.share.location.presentation.homenav.HomeNavSharedViewModel
import com.harian.closer.share.location.presentation.homenav.notification.adapter.FriendRequestAdapter
import com.harian.closer.share.location.presentation.search.SearchFragment
import com.harian.closer.share.location.utils.extension.Animation
import com.harian.closer.share.location.utils.extension.findGlobalNavController
import com.harian.closer.share.location.utils.extension.navigateWithAnimation
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentNotificationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class NotificationFragment : BaseFragment<FragmentNotificationBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_notification

    @Inject
    lateinit var friendRequestAdapter: FriendRequestAdapter

    private val viewModel: FriendRequestViewModel by viewModels()
    private val sharedViewModel by activityViewModels<HomeNavSharedViewModel>()

    override fun setupUI() {
        super.setupUI()
        setupRecyclerView()
        viewModel.fetchFriendRequest()
    }

    private fun setupRecyclerView() {
        friendRequestAdapter.setOnItemClickListener(object : FriendRequestAdapter.OnItemClickListener {
            override fun onClickItem(userEntity: UserEntity) {
                findGlobalNavController()?.navigateWithAnimation(
                    HomeNavFragmentDirections.actionHomeNavFragmentToProfileFragment(userEntity)
                )
            }

            override fun onClickAccept(userEntity: UserEntity) {
                viewModel.acceptFriendRequest(userEntity)
            }

            override fun onClickDeny(userEntity: UserEntity) {
                viewModel.denyFriendRequest(userEntity)
            }
        })
        binding.apply {
            rvFriendRequest.adapter = friendRequestAdapter
        }
    }

    override fun handleStateChanges() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is FriendRequestViewModel.ApiState.Init -> Unit
                is FriendRequestViewModel.ApiState.ErrorGetFriendRequest -> handleErrorGetFriendRequest()
                is FriendRequestViewModel.ApiState.SuccessGetFriendRequest -> handleSuccessGetFriendRequest(it.friendRequests)
                is FriendRequestViewModel.ApiState.ErrorAcceptFriendRequest -> handleErrorAcceptFriendRequest()
                is FriendRequestViewModel.ApiState.SuccessAcceptFriendRequest -> handleSuccessAcceptFriendRequest(it.user)
                is FriendRequestViewModel.ApiState.ErrorDenyFriendRequest -> handleErrorDenyFriendRequest()
                is FriendRequestViewModel.ApiState.SuccessDenyFriendRequest -> handleSuccessDenyFriendRequest(it.user)
            }
        }.launchIn(lifecycleScope)

        viewModel.loadingState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is FriendRequestViewModel.LoadingState.Init -> Unit
                is FriendRequestViewModel.LoadingState.CancelLoading -> friendRequestAdapter.cancelLoading(it.user)
                is FriendRequestViewModel.LoadingState.StartLoading -> friendRequestAdapter.startLoading(it.user)
            }
        }.launchIn(lifecycleScope)

        viewModel.globalLoadingState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            binding.frLoading.isVisible = it
        }.launchIn(lifecycleScope)

        sharedViewModel.centerActionButtonClickLiveData.observe(viewLifecycleOwner) {
            if (it) {
                findGlobalNavController()?.navigateWithAnimation(
                    HomeNavFragmentDirections.actionHomeNavFragmentToSearchFragment(SearchFragment.SearchType.USER.name),
                    Animation.SlideUp
                )
                sharedViewModel.resetCenterActionButtonClick()
            }
        }
    }

    private fun handleErrorDenyFriendRequest() {
        showToast("An error occurred while processing your request")
    }

    private fun handleSuccessDenyFriendRequest(user: UserEntity) {
        showToast("Friend request from ${user.name} is denied")
        friendRequestAdapter.removeFriendRequest(user)
    }

    private fun handleSuccessAcceptFriendRequest(user: UserEntity) {
        showToast("You and ${user.name} are friends now")
        friendRequestAdapter.removeFriendRequest(user)
    }

    private fun handleErrorAcceptFriendRequest() {
        showToast("An error occurred while processing your request")
    }

    private fun handleErrorGetFriendRequest() {
        showToast("Error get friend requests")
    }

    private fun handleSuccessGetFriendRequest(friendRequests: List<FriendRequestEntity>) {
        friendRequestAdapter.updateData(friendRequests)
        binding.icNoNotification.isVisible = friendRequests.isEmpty()
    }
}

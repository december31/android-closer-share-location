package com.harian.closer.share.location.presentation.homenav.profile

import android.annotation.SuppressLint
import android.os.Build
import android.view.WindowInsets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.harian.closer.share.location.domain.country.entity.CountryEntity
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.domain.user.entity.FriendsEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.presentation.homenav.HomeNavFragmentDirections
import com.harian.closer.share.location.presentation.homenav.HomeNavSharedViewModel
import com.harian.closer.share.location.utils.extension.Animation
import com.harian.closer.share.location.utils.extension.findGlobalNavController
import com.harian.closer.share.location.utils.extension.navigateWithAnimation
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_profile

    private val viewModel: ProfileViewModel by viewModels()
    private val sharedViewModel by activityViewModels<HomeNavSharedViewModel>()
    private val args: ProfileFragmentArgs by navArgs()
    private lateinit var adapter: ProfileAdapter

    @SuppressLint("WrongConstant")
    override fun setupSystemBarBehavior() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val insets = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.statusBars())
                binding.root.setPadding(0, 0, 0, insets.bottom)
            }
            WindowInsetsCompat.CONSUMED
        }
    }

    override fun setupUI() {
        super.setupUI()
        setupRecyclerView()
        handleStateChanges()
        viewModel.fetchUserInformation(args.user)
    }

    override fun setupListener() {
        super.setupListener()
        adapter.setOnClickAddFriendListener { user ->
            viewModel.sendFriendRequest(user)
            adapter.updateFriendRequestStatus(ProfileAdapter.SendFriendRequestStatus.SENDING_FRIEND_REQUEST)
        }

        adapter.setOnClickMessageListener {
            findGlobalNavController()?.navigateWithAnimation(
                ProfileFragmentDirections.actionProfileFragmentToMessageDetailFragment(it),
                Animation.SlideLeft
            )
        }

        adapter.setOnClickUserAvatarListener {
            if (args.user == null) {
                findGlobalNavController()?.navigateWithAnimation(
                    HomeNavFragmentDirections.actionHomeNavFragmentToProfileFragment(it),
                    Animation.SlideLeft
                )
            } else {
                findGlobalNavController()?.navigateWithAnimation(
                    ProfileFragmentDirections.actionProfileFragmentSelf(it),
                    Animation.SlideLeft
                )
            }
        }

        sharedViewModel.centerActionButtonClickLiveData.observe(viewLifecycleOwner) {
            if (it) {
                findGlobalNavController()?.navigateWithAnimation(
                    HomeNavFragmentDirections.actionHomeNavFragmentToScanQrCodeFragment(),
                    Animation.SlideUp
                )
                sharedViewModel.resetCenterActionButtonClick()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.saveState(adapter.getData())
    }

    private fun setupRecyclerView() {
        adapter = ProfileAdapter(viewModel.sharedPrefs.getToken())
        binding.rv.adapter = adapter
    }

    private fun handleStateChanges() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is ProfileViewModel.ProfileState.Init -> adapter.updateData(it.defaultData)
                is ProfileViewModel.ProfileState.ErrorGetUserInformation -> handleErrorGetUserInformation()
                is ProfileViewModel.ProfileState.SuccessGetUserInformation -> handleSuccessGetUserInformation(it.user)
                is ProfileViewModel.ProfileState.ErrorGetCountry -> Unit
                is ProfileViewModel.ProfileState.SuccessGetCountry -> handleSuccessGetCountry(it.country)
                is ProfileViewModel.ProfileState.SuccessGetFriends -> handleSuccessGetFriend(it.friends)
                is ProfileViewModel.ProfileState.ErrorGetFriends -> handleErrorGetFriend()
                is ProfileViewModel.ProfileState.ErrorGetPosts -> Unit
                is ProfileViewModel.ProfileState.SuccessGetPosts -> handleSuccessGetPosts(it.posts)
                is ProfileViewModel.ProfileState.ErrorSendFriendRequest -> handleErrorSendFriendRequest()
                is ProfileViewModel.ProfileState.SuccessSendFriendRequest -> handleSuccessSendFriendRequest()
            }
        }.launchIn(lifecycleScope)
    }

    private fun handleSuccessSendFriendRequest() {
        adapter.updateFriendRequestStatus(ProfileAdapter.SendFriendRequestStatus.FRIEND_REQUEST_SENT)
        showToast(R.string.friend_request_sent)
    }

    private fun handleErrorSendFriendRequest() {
        adapter.updateFriendRequestStatus(ProfileAdapter.SendFriendRequestStatus.FAILED_SENDING_FRIEND_REQUEST)
        showToast(R.string.failed_sending_friend_request_please_try_again_later)
    }

    private fun handleSuccessGetPosts(posts: List<PostEntity>) {
        adapter.updatePosts(posts)
    }

    private fun handleSuccessGetFriend(friends: FriendsEntity) {
        adapter.updateFriends(friends)
    }

    private fun handleErrorGetFriend() {

    }

    private fun handleSuccessGetCountry(@Suppress("UNUSED_PARAMETER") country: CountryEntity) {

    }

    private fun handleSuccessGetUserInformation(user: UserEntity) {
        adapter.updateProfile(user)
    }

    private fun handleErrorGetUserInformation() {

    }
}

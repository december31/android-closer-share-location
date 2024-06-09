package com.harian.closer.share.location.presentation.viewall.friend

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.harian.closer.share.location.domain.user.entity.FriendEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.platform.SharedPrefs
import com.harian.closer.share.location.utils.extension.glideLoadImage
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentViewAllFriendBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class ViewAllFriendFragment : BaseFragment<FragmentViewAllFriendBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_view_all_friend

    @Inject
    lateinit var friendAdapter: FriendAdapter

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private val viewModel: FriendViewModel by viewModels()
    private val args: ViewAllFriendFragmentArgs by navArgs()

    override fun getFragmentViewModel() = viewModel

    override fun setupUI() {
        super.setupUI()
        setupRecyclerView()
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            icClose.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.apply {
            rvFriends.adapter = friendAdapter
        }
    }

    override fun handleStateChanges() {
        viewModel.friendsStates.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is FriendViewModel.ApiState.Init -> Unit
                is FriendViewModel.ApiState.ErrorGetFriends -> handleErrorGetFriends()
                is FriendViewModel.ApiState.SuccessGetFriends -> handleSuccessGetFriends(it.friends)
                else -> Unit
            }
        }.launchIn(lifecycleScope)

        viewModel.userInformationStates.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is FriendViewModel.ApiState.Init -> Unit
                is FriendViewModel.ApiState.ErrorGetUserInformation -> Unit
                is FriendViewModel.ApiState.SuccessGetUserInformation -> handleSuccessGetUserInformation(it.user)
                else -> Unit
            }

        }.launchIn(lifecycleScope)

        viewModel.fetchFriends(args.user)
    }

    private fun handleErrorGetFriends() {
        showToast(R.string.some_thing_went_wrong_please_try_again_later)
    }

    private fun handleSuccessGetFriends(friends: List<FriendEntity>) {
        friendAdapter.updateData(friends)
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessGetUserInformation(user: UserEntity) {
        binding.apply {
            if (args.user == null) {
                title.text = getString(R.string.your_friends)
            } else {
                title.text = "${args.user?.name}'s ${getString(R.string.friends)}"
            }

            imgAvatar.glideLoadImage(user.getAuthorizedAvatarUrl(sharedPrefs.getToken()))
        }
    }
}


package com.harian.closer.share.location.presentation.mainnav.profile

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.domain.user.entity.FriendsEntity
import com.harian.closer.share.location.domain.user.entity.ProfileEntity
import com.harian.closer.share.location.domain.user.entity.ProfileType
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.platform.BaseRecyclerViewAdapter
import com.harian.closer.share.location.presentation.mainnav.friend.FriendAdapter
import com.harian.closer.share.location.presentation.mainnav.home.PostAdapter
import com.harian.closer.share.location.utils.extension.glideLoadImage
import com.harian.closer.share.location.utils.extension.gone
import com.harian.closer.share.location.utils.extension.visible
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.ItemRecyclerFriendsBinding
import com.harian.software.closer.share.location.databinding.ItemRecyclerProfileBinding
import com.harian.software.closer.share.location.databinding.ItemRecyclerProfilePostsBinding

class ProfileAdapter(private val bearerToken: String) : BaseRecyclerViewAdapter<ProfileEntity<Any>, ViewDataBinding>() {

    private val friendAdapter: FriendAdapter = FriendAdapter(bearerToken)
    private val postAdapter: PostAdapter = PostAdapter(bearerToken)

    private var onclickAddFriend: (UserEntity) -> Unit = {}
    private var onclickMessage: (UserEntity) -> Unit = {}

    fun setOnClickAddFriendListener(onclickAddFriend: (UserEntity) -> Unit) {
        this.onclickAddFriend = onclickAddFriend
    }

    fun setOnClickMessageListener(onclickMessage: (UserEntity) -> Unit) {
        this.onclickMessage = onclickMessage
    }

    override fun getLayoutId(viewType: Int): Int {
        return when (viewType) {
            ProfileType.PROFILE.viewType -> R.layout.item_recycler_profile
            ProfileType.FRIENDS.viewType -> R.layout.item_recycler_friends
            else -> R.layout.item_recycler_profile_posts
        }
    }

    override fun getItemViewType(position: Int): Int {
        return items.getOrNull(position)?.type?.viewType ?: ProfileType.PROFILE.viewType
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewDataBinding, ProfileEntity<Any>>, position: Int) {
        items.getOrNull(position)?.let {
            bindProfile(holder, it)
            bindFriends(holder, it)
            bindPosts(holder, it)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewDataBinding, ProfileEntity<Any>>, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains(SendFriendRequestStatus.SENDING_FRIEND_REQUEST)) {
            updateFriendRequestStatus(holder, SendFriendRequestStatus.SENDING_FRIEND_REQUEST)
        } else if (payloads.contains(SendFriendRequestStatus.FRIEND_REQUEST_SENT)) {
            updateFriendRequestStatus(holder, SendFriendRequestStatus.FRIEND_REQUEST_SENT)
        } else if (payloads.contains(SendFriendRequestStatus.FAILED_SENDING_FRIEND_REQUEST)) {
            updateFriendRequestStatus(holder, SendFriendRequestStatus.FAILED_SENDING_FRIEND_REQUEST)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getItemCount() = items.size

    private fun bindProfile(holder: BaseViewHolder<ViewDataBinding, ProfileEntity<Any>>, item: ProfileEntity<Any>?) {
        (holder.binding as? ItemRecyclerProfileBinding)?.apply {
            (item?.data as? UserEntity)?.let { user ->
                avatar.glideLoadImage(user.getAuthorizedAvatarUrl(bearerToken))
                username.text = user.name
                btnAddFriend.isVisible = user.isFriend == false
                btnMessage.isVisible = user.isFriend == true

                btnAddFriend.setOnClickListener {
                    onclickAddFriend.invoke(user)
                }

                btnMessage.setOnClickListener {
                    onclickMessage.invoke(user)
                }
            }
        }
    }

    fun updateFriendRequestStatus(status: SendFriendRequestStatus) {
        items.indexOfFirst { item -> item.type == ProfileType.PROFILE }.let { index ->
            notifyItemChanged(index, status)
        }
    }

    private fun updateFriendRequestStatus(holder: BaseViewHolder<ViewDataBinding, ProfileEntity<Any>>, status: SendFriendRequestStatus) {
        (holder.binding as? ItemRecyclerProfileBinding)?.apply {
            when (status) {
                SendFriendRequestStatus.SENDING_FRIEND_REQUEST -> {
                    loadingAnimation.visible()
                    btnAddFriend.setIconTintResource(R.color.primary)
                    btnAddFriend.setTextColor(ContextCompat.getColor(root.context, R.color.primary))
                    btnAddFriend.setBackgroundColor(ContextCompat.getColor(root.context, R.color.primary))
                }

                SendFriendRequestStatus.FRIEND_REQUEST_SENT -> {
                    loadingAnimation.gone()
                    btnAddFriend.setIconResource(R.drawable.ic_friend_request_sent)
                    btnAddFriend.setIconTintResource(R.color.white)
                    btnAddFriend.setTextColor(ContextCompat.getColor(root.context, R.color.white))
                    btnAddFriend.setBackgroundColor(ContextCompat.getColor(root.context, R.color.text_placeholder))
                    btnAddFriend.setText(R.string.friend_request_sent)
                }

                SendFriendRequestStatus.FAILED_SENDING_FRIEND_REQUEST -> {
                    loadingAnimation.gone()
                    btnAddFriend.setIconResource(R.drawable.ic_add_friend)
                    btnAddFriend.setIconTintResource(R.color.white)
                    btnAddFriend.setTextColor(ContextCompat.getColor(root.context, R.color.white))
                    btnAddFriend.setBackgroundColor(ContextCompat.getColor(root.context, R.color.primary))
                }
            }
        }
    }

    private fun bindFriends(holder: BaseViewHolder<ViewDataBinding, ProfileEntity<Any>>, item: ProfileEntity<Any>?) {
        (holder.binding as? ItemRecyclerFriendsBinding)?.apply {
            (item?.data as? FriendsEntity)?.let { friendsEntity ->
                loading.gone()
                loading.cancelAnimation()
                rvFriends.visible()
                tvFriendCount.text = friendsEntity.count.toString()
                rvFriends.adapter = friendAdapter
                friendAdapter.updateData(friendsEntity.friends)
            }
        }
    }

    private fun bindPosts(holder: BaseViewHolder<ViewDataBinding, ProfileEntity<Any>>, item: ProfileEntity<Any>?) {
        (holder.binding as? ItemRecyclerProfilePostsBinding)?.apply {
            (item?.data as? List<*>)?.let { posts ->
                loading.gone()
                loading.cancelAnimation()
                rvPost.visible()
                tvPostsSize.text = posts.size.toString()
                rvPost.adapter = postAdapter
                postAdapter.updateData(posts.mapNotNull { post -> post as? PostEntity })
            }
        }
    }

    fun updateFriends(friends: FriendsEntity) {
        items.indexOfFirst { item -> item.type == ProfileType.FRIENDS }.let { index ->
            items.getOrNull(index)?.data = friends
            notifyItemChanged(index)
        }
    }

    fun updateProfile(user: UserEntity) {
        items.indexOfFirst { item -> item.type == ProfileType.PROFILE }.let { index ->
            items.getOrNull(index)?.data = user
            notifyItemChanged(index)
        }
    }

    fun updatePosts(posts: List<PostEntity>) {
        items.indexOfFirst { item -> item.type == ProfileType.POSTS }.let { index ->
            items.getOrNull(index)?.data = posts
            notifyItemChanged(index)
        }
    }

    enum class SendFriendRequestStatus {
        SENDING_FRIEND_REQUEST,
        FRIEND_REQUEST_SENT,
        FAILED_SENDING_FRIEND_REQUEST
    }
}

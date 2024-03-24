package com.harian.closer.share.location.presentation.mainnav.profile

import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import com.harian.closer.share.location.domain.post.entity.PostEntity
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

    private fun bindFriends(holder: BaseViewHolder<ViewDataBinding, ProfileEntity<Any>>, item: ProfileEntity<Any>?) {
        (holder.binding as? ItemRecyclerFriendsBinding)?.apply {
            (item?.data as? List<*>)?.let { friends ->
                loading.gone()
                loading.cancelAnimation()
                rvFriends.visible()
                tvFriendSize.text = friends.size.toString()
                rvFriends.adapter = friendAdapter
                friendAdapter.updateData(friends.mapNotNull { friend -> friend as UserEntity })
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

    fun updateFriends(friends: List<UserEntity>) {
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
}

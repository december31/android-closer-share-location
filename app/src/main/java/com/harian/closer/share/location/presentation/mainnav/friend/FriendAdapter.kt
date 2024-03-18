package com.harian.closer.share.location.presentation.mainnav.friend

import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.platform.BaseRecyclerViewAdapter
import com.harian.closer.share.location.utils.extension.glideLoadImage
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.ItemRecyclerGridFriendBinding

class FriendAdapter(private val bearerToken: String) : BaseRecyclerViewAdapter<UserEntity, ItemRecyclerGridFriendBinding>() {
    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_recycler_grid_friend
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemRecyclerGridFriendBinding, UserEntity>, position: Int) {
        holder.binding.apply {
            val item = items.getOrNull(position) ?: return
            avatar.glideLoadImage(item.getAuthorizedAvatarUrl(bearerToken))
            tvName.text = item.name
        }
    }
}
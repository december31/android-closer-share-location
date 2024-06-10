package com.harian.closer.share.location.presentation.friendrequest

import com.harian.closer.share.location.domain.user.entity.FriendRequestEntity
import com.harian.closer.share.location.platform.BaseRecyclerViewAdapter
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.ItemRecyclerFriendRequestBinding

class FriendRequestAdapter : BaseRecyclerViewAdapter<FriendRequestEntity, ItemRecyclerFriendRequestBinding>() {
    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_recycler_friend_request
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemRecyclerFriendRequestBinding, FriendRequestEntity>, position: Int) {

    }
}

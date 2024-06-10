package com.harian.closer.share.location.presentation.homenav.friend

import com.harian.closer.share.location.domain.user.entity.FriendEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.platform.BaseRecyclerViewAdapter
import com.harian.closer.share.location.utils.extension.glideLoadImage
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.ItemRecyclerGridFriendBinding

class FriendAdapter(private val bearerToken: String) : BaseRecyclerViewAdapter<FriendEntity, ItemRecyclerGridFriendBinding>() {

    private var onItemClick: ((UserEntity) -> Unit)? = null

    fun setOnItemClick(onItemClick: (UserEntity) -> Unit) {
        this.onItemClick = onItemClick
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_recycler_grid_friend
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemRecyclerGridFriendBinding, FriendEntity>, position: Int) {
        holder.binding.apply {
            val item = items.getOrNull(position) ?: return
            avatar.glideLoadImage(item.information.getAuthorizedAvatarUrl(bearerToken))
            tvName.text = item.information.name
            tvName.isSelected = true

            root.setOnClickListener {
                onItemClick?.invoke(item.information)
            }
        }
    }
}

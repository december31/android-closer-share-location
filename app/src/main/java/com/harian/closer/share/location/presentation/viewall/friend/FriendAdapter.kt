package com.harian.closer.share.location.presentation.viewall.friend

import com.harian.closer.share.location.domain.user.entity.FriendEntity
import com.harian.closer.share.location.platform.BaseRecyclerViewAdapter
import com.harian.closer.share.location.utils.extension.glideLoadImage
import com.harian.closer.share.location.utils.extension.gone
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.ItemUserBinding

class FriendAdapter(private val bearerToken: String) : BaseRecyclerViewAdapter<FriendEntity, ItemUserBinding>() {
    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_user
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemUserBinding, FriendEntity>, position: Int) {
        val item = items.getOrNull(position) ?: return
        holder.binding.apply {
            lnMutualFriends.gone()
            tvName.text = item.information.name
            tvAddress.text = item.information.address
            imgAvatar.glideLoadImage(item.information.getAuthorizedAvatarUrl(bearerToken))
        }
    }
}

package com.harian.closer.share.location.presentation.mainnav.profile

import androidx.databinding.ViewDataBinding
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.platform.BaseRecyclerViewAdapter
import com.harian.closer.share.location.utils.extension.glideLoadImage
import com.harian.software.closer.share.location.databinding.ItemRecyclerProfileBinding

class ProfileAdapter(private val bearerToken: String) : BaseRecyclerViewAdapter<Any, ViewDataBinding>() {

    override fun getLayoutId(viewType: Int): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewDataBinding, Any>, position: Int) {
        bindProfile(holder, items.getOrNull(position))
    }

    override fun getItemCount() = items.size

    private fun bindProfile(holder: BaseViewHolder<ViewDataBinding, Any>, item: Any?) {
        (holder.binding as? ItemRecyclerProfileBinding)?.apply {
            (item as? UserEntity)?.let { user ->
                avatar.glideLoadImage(user.getAuthorizedAvatarUrl(bearerToken))
                username.text = user.name
            }
        }
    }
}

package com.harian.closer.share.location.presentation.search

import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.platform.BaseRecyclerViewAdapter
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.ItemUserBinding

class UserAdapter(private val bearerToken: String) : BaseRecyclerViewAdapter<UserEntity, ViewDataBinding>() {
    private var onItemClick: ((UserEntity) -> Unit) = { }

    fun setOnItemClick(onItemClick: (UserEntity) -> Unit) {
        this.onItemClick = onItemClick
    }

    override fun getLayoutId(viewType: Int): Int = R.layout.item_user

    override fun onBindViewHolder(holder: BaseViewHolder<ViewDataBinding, UserEntity>, position: Int) {
        bindUser(holder.binding as? ItemUserBinding, items.getOrNull(position))
    }

    private fun bindUser(binding: ItemUserBinding?, user: UserEntity?) {
        user ?: return
        binding?.apply {
            tvName.text = user.name
            tvAddress.text = user.address
            Glide.with(root).load(user.getAuthorizedAvatarUrl(bearerToken)).into(imgAvatar)

            tvAddress.isVisible = !user.address.isNullOrEmpty()

            root.setOnClickListener {
                onItemClick.invoke(user)
            }
        }
    }
}

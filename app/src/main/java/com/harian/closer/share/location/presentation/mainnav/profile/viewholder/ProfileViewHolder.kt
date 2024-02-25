package com.harian.closer.share.location.presentation.mainnav.profile.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.utils.extension.glideLoadImage
import com.harian.software.closer.share.location.databinding.ItemRecyclerProfileBinding

class ProfileViewHolder(private val binding: ItemRecyclerProfileBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(user: UserEntity) {
        binding.apply {
            avatar.glideLoadImage(user.authorizedAvatarUrl)
            username.text = user.name
        }
    }
}

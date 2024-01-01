package com.harian.closer.share.location.presentation.post.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.utils.Constants
import com.harian.software.closer.share.location.databinding.ItemRecyclerDetailsPostBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class PostDetailsViewHolder(private val binding: ItemRecyclerDetailsPostBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: PostEntity?) {
        if (item == null) return
        binding.apply {
            Glide.with(binding.root).load(item.owner?.authorizedAvatarUrl).into(binding.imgAvatar)
            tvUsername.text = item.owner?.name
            item.createdTime?.let {
                tvCreatedTime.text = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date(it))
            }
            tvTitle.isVisible = !item.title.isNullOrBlank()
            tvTitle.text = item.title
            tvContent.isVisible = !item.content.isNullOrBlank()
            tvContent.text = item.content

            tvCommented.text = "${item.comments?.size ?: 0}"
            tvLiked.text = "${item.likes?.size ?: 0}"
            tvWatched.text = "${0}"
        }
    }

    companion object {
        fun newInstance(parent: ViewGroup): PostDetailsViewHolder {
            return PostDetailsViewHolder(
                ItemRecyclerDetailsPostBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}

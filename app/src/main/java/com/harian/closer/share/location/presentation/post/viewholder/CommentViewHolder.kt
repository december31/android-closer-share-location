package com.harian.closer.share.location.presentation.post.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.harian.closer.share.location.domain.comment.entity.CommentEntity
import com.harian.software.closer.share.location.databinding.ItemRecyclerCommentBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentViewHolder(private val binding: ItemRecyclerCommentBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: CommentEntity?) {
        if (item == null) return
        binding.apply {
            Glide.with(binding.root).load(item.owner?.authorizedAvatarUrl).into(binding.imgAvatar)
            tvUsername.text = item.owner?.name
            tvContent.text = item.content
            item.createdTime?.let {
                tvCreatedTime.text = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date(it))
            }
        }
    }

    companion object {
        fun newInstance(parent: ViewGroup): CommentViewHolder {
            return CommentViewHolder(
                ItemRecyclerCommentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}


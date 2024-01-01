package com.harian.closer.share.location.presentation.post.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.harian.closer.share.location.domain.post.entity.ImageEntity
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.ItemRecyclerPostImageBinding

class ImageViewHolder(private val binding: ItemRecyclerPostImageBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ImageEntity?) {
        if (item == null) return
        binding.apply {
            Glide.with(root.context)
                .load(item.authorizedUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.image_loading)
                .into(image)
            tvCommented.text = (item.comments?.size ?: 0).toString()
            tvLiked.text = (item.likes?.size ?: 0).toString()
        }
    }

    companion object {
        fun newInstance(parent: ViewGroup): ImageViewHolder {
            return ImageViewHolder(
                ItemRecyclerPostImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}

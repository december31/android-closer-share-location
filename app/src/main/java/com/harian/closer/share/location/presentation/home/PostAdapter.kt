package com.harian.closer.share.location.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.utils.Constants
import com.harian.software.closer.share.location.databinding.ItemRecyclerPostBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostAdapter : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val items = arrayListOf<PostEntity>()

    fun updateData(data: List<PostEntity>) {
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = items.size

            override fun getNewListSize() = data.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return items.getOrNull(oldItemPosition)?.id == data.getOrNull(newItemPosition)?.id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return data.getOrNull(newItemPosition)?.let { newItem ->
                    items.getOrNull(oldItemPosition)?.compareTo(newItem)
                } == 0
            }
        }).dispatchUpdatesTo(this)
        items.clear()
        items.addAll(data)
    }

    inner class PostViewHolder(private val binding: ItemRecyclerPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostEntity?) {
            if (item == null) return
            binding.apply {
                Glide.with(binding.root).load(item.owner?.avatar ?: Constants.DEFAULT_IMAGE_URL).into(binding.imgAvatar)
                tvUsername.text = item.owner?.name
                item.createdTime?.let {
                    tvCreatedTime.text = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date(it))
                }
                tvTitle.isVisible = !item.title.isNullOrBlank()
                tvTitle.text = item.title
                tvContent.isVisible = !item.content.isNullOrBlank()
                tvContent.text = item.content
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            ItemRecyclerPostBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(items.getOrNull(position))
    }
}

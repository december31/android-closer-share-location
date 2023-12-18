package com.harian.closer.share.location.presentation.post.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.harian.closer.share.location.domain.comment.entity.CommentEntity
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.utils.Constants
import com.harian.software.closer.share.location.databinding.ItemRecyclerCommentBinding
import com.harian.software.closer.share.location.databinding.ItemRecyclerDetailsPostBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * a adapter for display the post and its comments
 */
class PostDetailsAdapter : RecyclerView.Adapter<ViewHolder>() {

    private val items = arrayListOf<Any>()

    fun updateData(data: List<Any>) {
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = items.size
            override fun getNewListSize() = data.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return (items.getOrNull(oldItemPosition) as? CommentEntity)?.let { oldItem ->
                    (data.getOrNull(newItemPosition) as? CommentEntity)?.let { newItem ->
                        oldItem.id == newItem.id
                    }
                } ?: true
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return (items.getOrNull(oldItemPosition) as? CommentEntity)?.let { oldItem ->
                    (data.getOrNull(newItemPosition) as? CommentEntity)?.let { newItem ->
                        oldItem.compareTo(newItem) == 0
                    }
                } ?: true
            }

        }).dispatchUpdatesTo(this)
        this.items.clear()
        this.items.addAll(data)
    }

    inner class PostViewHolder(private val binding: ItemRecyclerDetailsPostBinding) : ViewHolder(binding.root) {
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

                tvCommented.text = "${item.comments?.size ?: 0}"
                tvLiked.text = "${item.likes?.size ?: 0}"
                tvWatched.text = "${0}"
            }
        }
    }

    inner class CommentViewHolder(private val binding: ItemRecyclerCommentBinding) : ViewHolder(binding.root) {
        fun bind(item: CommentEntity?) {
            if (item == null) return
            binding.apply {
                Glide.with(binding.root).load(item.owner?.avatar ?: Constants.DEFAULT_IMAGE_URL).into(binding.imgAvatar)
                tvUsername.text = item.owner?.name
                tvContent.text = item.content
                item.createdTime?.let {
                    tvCreatedTime.text = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date(it))
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) return 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            0 -> PostViewHolder(ItemRecyclerDetailsPostBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            1 -> CommentViewHolder(ItemRecyclerCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> CommentViewHolder(ItemRecyclerCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as? PostViewHolder)?.bind(items.getOrNull(position) as? PostEntity)
        (holder as? CommentViewHolder)?.bind(items.getOrNull(position) as? CommentEntity)
    }
}

package com.harian.closer.share.location.presentation.post.details

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.harian.closer.share.location.domain.comment.entity.CommentEntity
import com.harian.closer.share.location.domain.post.entity.ImageEntity
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.presentation.post.viewholder.ImageViewHolder
import com.harian.closer.share.location.presentation.post.viewholder.PostDetailsViewHolder

/**
 * a adapter for display the post and its images
 */
class PostDetailsAdapter : RecyclerView.Adapter<ViewHolder>() {

    private val items = arrayListOf<Any>()

    fun updateData(data: List<Any>) {
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = items.size
            override fun getNewListSize() = data.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return (items.getOrNull(oldItemPosition) as? ImageEntity)?.let { oldItem ->
                    (data.getOrNull(newItemPosition) as? ImageEntity)?.let { newItem ->
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

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) return 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            0 -> PostDetailsViewHolder.newInstance(parent)
            1 -> ImageViewHolder.newInstance(parent)
            else -> ImageViewHolder.newInstance(parent)
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as? PostDetailsViewHolder)?.bind(items.getOrNull(position) as? PostEntity)
        (holder as? ImageViewHolder)?.bind(items.getOrNull(position) as? ImageEntity)
    }
}

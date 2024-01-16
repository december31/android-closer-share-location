package com.harian.closer.share.location.presentation.mainnav.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.ItemRecyclerPostHasImagesBinding
import com.harian.software.closer.share.location.databinding.ItemRecyclerPostNoImageBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostAdapter : RecyclerView.Adapter<ViewHolder>() {

    private val items = arrayListOf<PostEntity>()
    private var onItemClickListener: (Int) -> Unit = {}
    private var onLikePostListener: (PostEntity) -> Unit = {}
    fun setOnItemClickListener(onItemClickListener: (Int) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    fun setOnLikePostListener(onLikePostListener: (PostEntity) -> Unit) {
        this.onLikePostListener = onLikePostListener
    }

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

    /**
     * this function shouldn't be run main thread
     */
    fun getItemPosition(postId: Int): Int {
        return items.indexOf(items.firstOrNull { item -> item.id == postId })
    }

    fun updatePostReactions(position: Int) {
        notifyItemChanged(position, PayLoad.REACTIONS)
    }

    inner class PostHasImagesViewHolder(private val binding: ItemRecyclerPostHasImagesBinding) : ViewHolder(binding.root) {
        fun bind(item: PostEntity?) {
            if (item == null) return
            binding.apply {
                root.setOnClickListener {
                    item.id?.let { id -> onItemClickListener.invoke(id) }
                }

                icLike.setOnClickListener {
                    item.isLiked = !item.isLiked
                    notifyItemChanged(adapterPosition, PayLoad.REACTIONS)
                    onLikePostListener.invoke(item)
                }

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

                multipleImagesView.loadImages(item.images?.map { image -> image.authorizedUrl })
            }
        }

        fun updateReactions(item: PostEntity?) {
            if (item == null) return
            binding.apply {
                tvCommented.text = "${item.comments?.size ?: 0}"
                tvLiked.text = "${item.likes?.size ?: 0}"
                tvWatched.text = "${0}"
                icLike.setImageResource(if (item.isLiked) R.drawable.ic_liked else R.drawable.ic_like)
            }
        }
    }

    inner class PostNoImagesViewHolder(private val binding: ItemRecyclerPostNoImageBinding) : ViewHolder(binding.root) {
        fun bind(item: PostEntity?) {
            if (item == null) return
            binding.apply {
                root.setOnClickListener {
                    item.id?.let { id -> onItemClickListener.invoke(id) }
                }

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

        fun updateReactions(item: PostEntity?) {
            if (item == null) return
            binding.apply {
                tvCommented.text = "${item.comments?.size ?: 0}"
                tvLiked.text = "${item.likes?.size ?: 0}"
                tvWatched.text = "${0}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            PostType.HAS_IMAGES.value -> PostHasImagesViewHolder(
                ItemRecyclerPostHasImagesBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

            PostType.NO_IMAGES.value -> PostNoImagesViewHolder(
                ItemRecyclerPostNoImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> PostNoImagesViewHolder(
                ItemRecyclerPostNoImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].images.isNullOrEmpty()) PostType.NO_IMAGES.value else PostType.HAS_IMAGES.value
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as? PostHasImagesViewHolder)?.bind(items.getOrNull(position))
        (holder as? PostNoImagesViewHolder)?.bind(items.getOrNull(position))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains(PayLoad.REACTIONS)) {
            (holder as? PostHasImagesViewHolder)?.updateReactions(items.getOrNull(position))
            (holder as? PostNoImagesViewHolder)?.updateReactions(items.getOrNull(position))
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    enum class PayLoad {
        REACTIONS
    }

    enum class PostType(val value: Int) {
        HAS_IMAGES(1),
        NO_IMAGES(0)
    }
}

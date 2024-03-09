package com.harian.closer.share.location.presentation.mainnav.home

import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.platform.BaseRecyclerViewAdapter
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.ItemRecyclerPostHasImagesBinding
import com.harian.software.closer.share.location.databinding.ItemRecyclerPostNoImageBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostAdapter(private val bearerToken: String) : BaseRecyclerViewAdapter<PostEntity, ViewDataBinding>() {

    private var onItemClickListener: (Int) -> Unit = {}
    private var onLikePostListener: (PostEntity) -> Unit = {}
    fun setOnItemClickListener(onItemClickListener: (Int) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    fun setOnLikePostListener(onLikePostListener: (PostEntity) -> Unit) {
        this.onLikePostListener = onLikePostListener
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

    private fun bindHasImagePost(holder: BaseViewHolder<ViewDataBinding, PostEntity>, item: PostEntity?) {
        item ?: return
        (holder.binding as? ItemRecyclerPostHasImagesBinding)?.apply {
            root.setOnClickListener {
                item.id?.let { id -> onItemClickListener.invoke(id) }
            }

            icLike.setOnClickListener {
                item.isLiked = !item.isLiked
                notifyItemChanged(holder.adapterPosition, PayLoad.REACTIONS)
                onLikePostListener.invoke(item)
            }

            Glide.with(root).load(item.owner?.getAuthorizedAvatarUrl(bearerToken)).into(imgAvatar)

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

            multipleImagesView.loadImages(item.images?.map { image -> image.getAuthorizeUrl(bearerToken) })
        }
    }

    fun updateReactions(holder: BaseViewHolder<ViewDataBinding, PostEntity>, item: PostEntity?) {
        if (item == null) return
        (holder.binding as? ItemRecyclerPostHasImagesBinding)?.apply {
            tvCommented.text = "${item.comments?.size ?: 0}"
            tvLiked.text = "${item.likes?.size ?: 0}"
            tvWatched.text = "${0}"
            icLike.setImageResource(if (item.isLiked) R.drawable.ic_liked else R.drawable.ic_like)
        }
        (holder.binding as? ItemRecyclerPostNoImageBinding)?.apply {
            tvCommented.text = "${item.comments?.size ?: 0}"
            tvLiked.text = "${item.likes?.size ?: 0}"
            tvWatched.text = "${0}"
            icLike.setImageResource(if (item.isLiked) R.drawable.ic_liked else R.drawable.ic_like)
        }
    }

    fun bindNoImagePost(holder: BaseViewHolder<ViewDataBinding, PostEntity>, item: PostEntity?) {
        if (item == null) return
        (holder.binding as? ItemRecyclerPostNoImageBinding)?.apply {
            root.setOnClickListener {
                item.id?.let { id -> onItemClickListener.invoke(id) }
            }

            Glide.with(root).load(item.owner?.getAuthorizedAvatarUrl(bearerToken)).into(imgAvatar)
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

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getLayoutId(viewType: Int): Int {
        return when (viewType) {
            PostType.HAS_IMAGES.value -> R.layout.item_recycler_post_has_images
            PostType.NO_IMAGES.value -> R.layout.item_recycler_post_no_image
            else -> R.layout.item_recycler_post_no_image
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewDataBinding, PostEntity>, position: Int) {
        bindHasImagePost(holder, items.getOrNull(position))
        bindNoImagePost(holder, items.getOrNull(position))
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].images.isNullOrEmpty()) PostType.NO_IMAGES.value else PostType.HAS_IMAGES.value
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewDataBinding, PostEntity>, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains(PayLoad.REACTIONS)) {
            updateReactions(holder, items.getOrNull(position))
            updateReactions(holder, items.getOrNull(position))
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

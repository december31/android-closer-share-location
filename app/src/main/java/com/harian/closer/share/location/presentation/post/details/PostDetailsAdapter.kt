package com.harian.closer.share.location.presentation.post.details

import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.harian.closer.share.location.domain.post.entity.ImageEntity
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.platform.BaseRecyclerViewAdapter
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.ItemRecyclerDetailsPostBinding
import com.harian.software.closer.share.location.databinding.ItemRecyclerPostImageBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * a adapter for display the post and its images
 */
class PostDetailsAdapter(private val bearerToken: String) : BaseRecyclerViewAdapter<Any, ViewDataBinding>() {

    private var listener: Listener? = null
    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) return PostDetailsItemType.DETAIL.value else PostDetailsItemType.IMAGE.value
    }

    override fun getLayoutId(viewType: Int): Int {
        return when (viewType) {
            PostDetailsItemType.DETAIL.value -> R.layout.item_recycler_details_post
            PostDetailsItemType.IMAGE.value -> R.layout.item_recycler_post_image
            else -> R.layout.item_recycler_details_post
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewDataBinding, Any>, position: Int) {
        items.getOrNull(position).let { item ->
            bindPostDetail(holder, item)
            bindImage(holder, item)
        }
    }

    private fun bindPostDetail(holder: BaseViewHolder<ViewDataBinding, Any>, item: Any?) {
        (item as? PostEntity)?.let { post ->
            (holder.binding as ItemRecyclerDetailsPostBinding).apply {
                Glide.with(root).load(post.owner?.getAuthorizedAvatarUrl(bearerToken)).into(imgAvatar)
                tvUsername.text = post.owner?.name
                post.createdTime?.let {
                    tvCreatedTime.text = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date(it))
                }
                tvTitle.isVisible = !post.title.isNullOrBlank()
                tvTitle.text = post.title
                tvContent.isVisible = !post.content.isNullOrBlank()
                tvContent.text = post.content

                tvCommented.text = "${post.comments?.size ?: 0}"
                tvLiked.text = "${post.likes?.size ?: 0}"
                tvWatched.text = "${0}"

                containerComment.setOnClickListener {
                    listener?.onClickComment(post)
                }
                containerLiked.setOnClickListener {
                    listener?.onClickLike(post)
                }
            }
        }
    }

    private fun bindImage(holder: BaseViewHolder<ViewDataBinding, Any>, item: Any?) {
        (item as? ImageEntity)?.let { image ->
            (holder.binding as? ItemRecyclerPostImageBinding)?.apply {
                Glide.with(root.context)
                    .load(image.getAuthorizeUrl(bearerToken))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.empty)
                    .into(this.image)
//                tvCommented.text = (image.comments?.size ?: 0).toString()
//                tvLiked.text = (image.likes?.size ?: 0).toString()
                root.setOnClickListener {
                    listener?.onClickImage(image)
                }
            }
        }
    }

    override fun getItemCount() = items.size

    private enum class PostDetailsItemType(val value: Int) {
        DETAIL(0),
        IMAGE(1)
    }

    interface Listener {
        fun onClickImage(image: ImageEntity)
        fun onClickComment(postEntity: PostEntity)
        fun onClickLike(postEntity: PostEntity)
    }
}

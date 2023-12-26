package com.harian.closer.share.location.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.utils.Constants
import com.harian.closer.share.location.utils.extension.toBearerToken
import com.harian.software.closer.share.location.BuildConfig
import com.harian.software.closer.share.location.databinding.ItemRecyclerPostHasImagesBinding
import com.harian.software.closer.share.location.databinding.ItemRecyclerPostNoImageBinding
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostAdapter(private val token: String) : RecyclerView.Adapter<ViewHolder>() {

    private val items = arrayListOf<PostEntity>()
    private var onItemClickListener: (Int) -> Unit = {}
    fun setOnItemClickListener(onItemClickListener: (Int) -> Unit) {
        this.onItemClickListener = onItemClickListener
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

    inner class PostHasImagesViewHolder(private val binding: ItemRecyclerPostHasImagesBinding) : ViewHolder(binding.root) {
        fun bind(item: PostEntity?) {
            if (item == null) return
            binding.apply {
                root.setOnClickListener {
                    item.id?.let { id -> onItemClickListener.invoke(id) }
                }

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

                val authorizedUrls = item.imageUrls?.map {
                    val url = BuildConfig.API_BASE_URL + it
                    GlideUrl(url, LazyHeaders.Builder().addHeader(Constants.AUTHORIZATION, token.toBearerToken()).build())
                } ?: listOf()
                multipleImagesView.loadImages(authorizedUrls)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            POST_TYPE.HAS_IMAGES.value -> PostHasImagesViewHolder(
                ItemRecyclerPostHasImagesBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

            POST_TYPE.NO_IMAGES.value -> PostNoImagesViewHolder(
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
        return if (items[position].imageUrls.isNullOrEmpty()) POST_TYPE.NO_IMAGES.value else POST_TYPE.HAS_IMAGES.value
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as? PostHasImagesViewHolder)?.bind(items.getOrNull(position))
        (holder as? PostNoImagesViewHolder)?.bind(items.getOrNull(position))
    }

    enum class POST_TYPE(val value: Int) {
        HAS_IMAGES(1),
        NO_IMAGES(0)
    }
}

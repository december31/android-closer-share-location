package com.harian.closer.share.location.presentation.imagesviewer

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.harian.closer.share.location.domain.post.entity.ImageEntity
import com.harian.closer.share.location.platform.BaseRecyclerViewAdapter
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.ItemRecyclerImagesViewerBinding

class ImagesViewerAdapter(private val bearerToken: String) : BaseRecyclerViewAdapter<ImageEntity, ItemRecyclerImagesViewerBinding>() {
    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_recycler_images_viewer
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemRecyclerImagesViewerBinding, ImageEntity>, position: Int) {
        val item = items.getOrNull(position)
        holder.binding.apply {
            Glide.with(root.context)
                .load(item?.getAuthorizeUrl(bearerToken))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(image)
        }
    }
}

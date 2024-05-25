package com.harian.closer.share.location.presentation.post.comment

import com.harian.closer.share.location.domain.comment.entity.CommentEntity
import com.harian.closer.share.location.platform.BaseRecyclerViewAdapter
import com.harian.closer.share.location.utils.extension.glideLoadImage
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.ItemPostCommentBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentAdapter(private val bearerToken: String) : BaseRecyclerViewAdapter<CommentEntity, ItemPostCommentBinding>() {
    override fun getLayoutId(viewType: Int) = R.layout.item_post_comment

    override fun onBindViewHolder(holder: BaseViewHolder<ItemPostCommentBinding, CommentEntity>, position: Int) {
        val item = items.getOrNull(position) ?: return
        holder.binding.apply {
            imgAvatar.glideLoadImage(item.owner?.getAuthorizedAvatarUrl(bearerToken))
            tvUsername.text = item.owner?.name
            tvContent.text = item.content
            item.createdTime?.let {
                tvCreatedTime.text = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.US).format(Date(it))
            }
        }
    }
}

package com.harian.closer.share.location.presentation.message

import android.content.res.ColorStateList
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.harian.closer.share.location.domain.message.entity.MessageEntity
import com.harian.closer.share.location.platform.BaseRecyclerViewAdapter
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.ItemMessageBinding

class MessageDetailAdapter : BaseRecyclerViewAdapter<MessageEntity, ItemMessageBinding>() {
    override fun getLayoutId(viewType: Int): Int = R.layout.item_message

    override fun getItemViewType(position: Int): Int {
        return items.getOrNull(position)?.type?.value ?: MessageEntity.Type.SEND.value
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemMessageBinding, MessageEntity>, position: Int) {
        val message = items.getOrNull(position) ?: return
        holder.binding.apply {
            tvMessage.text = message.message
            tvMessage.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    root.context,
                    if (message.type == MessageEntity.Type.SEND) R.color.primary else R.color.gray_1
                )
            )
            tvMessage.setTextColor(
                ContextCompat.getColor(
                    root.context,
                    if (message.type == MessageEntity.Type.SEND) R.color.white else R.color.text_content
                )
            )
            tvMessage.layoutParams = (tvMessage.layoutParams as? ConstraintLayout.LayoutParams)?.apply {
                horizontalBias = if (message.type == MessageEntity.Type.SEND) 1f else 0f
            }
            tvMessage.requestLayout()
        }
    }
}

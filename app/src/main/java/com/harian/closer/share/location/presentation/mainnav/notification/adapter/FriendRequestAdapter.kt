package com.harian.closer.share.location.presentation.mainnav.notification.adapter

import com.harian.closer.share.location.domain.user.entity.FriendRequestEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.platform.BaseRecyclerViewAdapter
import com.harian.closer.share.location.utils.extension.glideLoadImage
import com.harian.closer.share.location.utils.extension.gone
import com.harian.closer.share.location.utils.extension.invisible
import com.harian.closer.share.location.utils.extension.visible
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.ItemRecyclerFriendRequestBinding

class FriendRequestAdapter(private val token: String) : BaseRecyclerViewAdapter<FriendRequestEntity, ItemRecyclerFriendRequestBinding>() {

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_recycler_friend_request
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemRecyclerFriendRequestBinding, FriendRequestEntity>, position: Int) {
        val item = items.getOrNull(position) ?: return
        holder.binding.apply {
            avatar.glideLoadImage(item.requestor.getAuthorizedAvatarUrl(token))
            name.text = item.requestor.name
            lnMutualFriends.invisible()

            root.setOnClickListener {
                onItemClickListener?.onClickItem(item.requestor)
            }

            btnAccept.setOnClickListener {
                onItemClickListener?.onClickAccept(item.requestor)
            }

            btnDeny.setOnClickListener {
                onItemClickListener?.onClickDeny(item.requestor)
            }
        }
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemRecyclerFriendRequestBinding, FriendRequestEntity>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        holder.binding.apply {
            if (payloads.contains(PayLoad.StartLoading)) {
                btnDeny.gone()
                btnAccept.text = null
                loadingAnimation.visible()
                loadingAnimation.playAnimation()
            } else if (payloads.contains(PayLoad.CancelLoading)) {
                btnDeny.visible()
                btnAccept.text = root.context.getString(R.string.accept)
                loadingAnimation.gone()
                loadingAnimation.cancelAnimation()
            } else {
                super.onBindViewHolder(holder, position, payloads)
            }
        }
    }

    fun removeFriendRequest(user: UserEntity) {
        updateData(items.filter { item -> item.requestor.id != user.id })
    }

    fun startLoading(user: UserEntity) {
        items.forEachIndexed { index, item ->
            if (item.requestor.id == user.id) {
                notifyItemChanged(index, PayLoad.StartLoading)
                return@forEachIndexed
            }
        }
    }

    fun cancelLoading(user: UserEntity) {
        items.forEachIndexed { index, item ->
            if (item.requestor.id == user.id) {
                notifyItemChanged(index, PayLoad.CancelLoading)
                return@forEachIndexed
            }
        }
    }

    enum class PayLoad {
        StartLoading,
        CancelLoading
    }

    interface OnItemClickListener {
        fun onClickItem(userEntity: UserEntity)
        fun onClickAccept(userEntity: UserEntity)
        fun onClickDeny(userEntity: UserEntity)
    }
}

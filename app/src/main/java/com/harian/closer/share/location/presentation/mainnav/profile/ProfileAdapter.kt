package com.harian.closer.share.location.presentation.mainnav.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.presentation.mainnav.profile.viewholder.ProfileViewHolder
import com.harian.software.closer.share.location.databinding.ItemRecyclerProfileBinding

class ProfileAdapter : RecyclerView.Adapter<ViewHolder>() {

    private val items = arrayListOf<Any>()

    fun updateData(data: List<Any>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ProfileViewHolder(ItemRecyclerProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (items[position] as? UserEntity)?.let { (holder as? ProfileViewHolder)?.bind(it) }
    }

}

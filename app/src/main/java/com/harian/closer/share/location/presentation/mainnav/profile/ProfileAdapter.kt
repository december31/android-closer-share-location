package com.harian.closer.share.location.presentation.mainnav.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.harian.closer.share.location.presentation.mainnav.profile.viewholder.ProfileViewHolder
import com.harian.software.closer.share.location.databinding.ItemRecyclerProfileBinding

class ProfileAdapter : RecyclerView.Adapter<ViewHolder>() {

    private val items = arrayListOf<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ProfileViewHolder(ItemRecyclerProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

}

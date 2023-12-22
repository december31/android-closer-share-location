package com.harian.closer.share.location.presentation.post

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.harian.software.closer.share.location.databinding.ItemRecyclerImageBinding

class ImagesAdapter : RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>() {

    private val items = arrayListOf<Uri>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(uris: ArrayList<Uri>) {
        items.clear()
        items.addAll(uris)
        notifyDataSetChanged()
    }

    inner class ImageViewHolder(private val binding: ItemRecyclerImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(uri: Uri) {
            Glide.with(binding.root.context).load(uri).into(binding.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        Log.d(this.javaClass.simpleName, "onCreateViewHolder()")
        return ImageViewHolder(ItemRecyclerImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = items.size
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        items.getOrNull(position)?.let { holder.bind(it) }
    }
}

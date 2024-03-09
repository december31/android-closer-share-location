package com.harison.core.app.platform

import androidx.recyclerview.widget.DiffUtil

class BaseDiffCallBack<T>(
    private val oldTask: List<T>,
    private val newTask: List<T>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldTask.size
    override fun getNewListSize(): Int = newTask.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldTask[oldItemPosition].hashCode() == newTask[newItemPosition].hashCode()
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldTask[oldItemPosition] == newTask[newItemPosition]
    }
}
package com.harian.closer.share.location.domain.comment.entity

import android.os.Parcelable
import com.harian.closer.share.location.domain.user.entity.UserEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommentEntity(
    val id: Int?,
    val content: String?,
    val createdTime: Long?,
    val owner: UserEntity?,
) : Comparable<CommentEntity>, Parcelable {
    override fun compareTo(other: CommentEntity): Int {
        return if (this.id == other.id &&
            this.content == other.content &&
            this.createdTime == other.createdTime
        ) 0 else 1
    }
}

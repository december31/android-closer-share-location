package com.harian.closer.share.location.domain.user.entity

import android.os.Parcelable
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import com.harian.closer.share.location.domain.common.base.BaseEntity
import com.harian.closer.share.location.utils.Constants
import com.harian.software.closer.share.location.BuildConfig
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserEntity(
    val id: Long?,
    val name: String?,
    val email: String?,
    val avatar: String?,
    val gender: String?,
    val description: String?,
    val status: String?,
    val isFriend: Boolean?,
    var latitude: Double?,
    var longitude: Double?,
) : Parcelable, BaseEntity<UserDTO> {

    @IgnoredOnParcel
    private var authorizedAvatarUrl: GlideUrl? = null

    fun getAuthorizedAvatarUrl(bearerToken: String): GlideUrl? {
        if (authorizedAvatarUrl == null) {
            authorizedAvatarUrl =
                GlideUrl(BuildConfig.API_BASE_URL + avatar, LazyHeaders.Builder().addHeader(Constants.AUTHORIZATION, bearerToken).build())
        }
        return authorizedAvatarUrl
    }

    override fun toDTO(): UserDTO {
        return UserDTO(
            id = id,
            name = name,
            email = email,
            avatar = avatar,
            gender = gender,
            description = description,
            status = status,
            isFriend = isFriend,
            latitude = latitude,
            longitude = longitude
        )
    }
}

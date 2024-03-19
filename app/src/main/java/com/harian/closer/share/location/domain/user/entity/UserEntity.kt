package com.harian.closer.share.location.domain.user.entity

import android.os.Parcelable
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.google.android.gms.maps.model.MarkerOptions
import com.harian.closer.share.location.utils.Constants
import com.harian.software.closer.share.location.BuildConfig
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserEntity(
    val id: Int?,
    val name: String?,
    val email: String?,
    val avatar: String?,
    val gender: String?,
    val description: String?,
    var latitude: Double?,
    var longitude: Double?
) : Parcelable {

    @IgnoredOnParcel
    private var authorizedAvatarUrl: GlideUrl? = null

    fun getAuthorizedAvatarUrl(bearerToken: String): GlideUrl? {
        if (authorizedAvatarUrl == null) {
            authorizedAvatarUrl =
                GlideUrl(BuildConfig.API_BASE_URL + avatar, LazyHeaders.Builder().addHeader(Constants.AUTHORIZATION, bearerToken).build())
        }
        return authorizedAvatarUrl
    }
}

package com.harian.closer.share.location.data.user.remote.dto

import com.google.gson.annotations.SerializedName
import com.harian.closer.share.location.domain.user.entity.UserEntity

data class UserDTO(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("is-friend") val isFriend: Boolean?,
    @SerializedName("status") val status: String?,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
) {
    companion object {
        fun fromUserEntity(userEntity: UserEntity): UserDTO {
            return UserDTO(
                id = userEntity.id,
                name = userEntity.name,
                email = userEntity.email,
                avatar = userEntity.avatar,
                gender = userEntity.gender,
                description = userEntity.description,
                isFriend = userEntity.isFriend,
                status = userEntity.status,
                latitude = userEntity.latitude,
                longitude = userEntity.longitude,
            )
        }
    }
}
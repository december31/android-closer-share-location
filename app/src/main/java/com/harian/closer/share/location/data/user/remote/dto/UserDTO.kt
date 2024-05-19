package com.harian.closer.share.location.data.user.remote.dto

import com.google.gson.annotations.SerializedName
import com.harian.closer.share.location.data.common.base.BaseDTO
import com.harian.closer.share.location.domain.user.entity.UserEntity

data class UserDTO(
    @SerializedName("id") val id: Long?,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("is-friend") val isFriend: Boolean?,
    @SerializedName("status") val status: String?,
    @SerializedName("phone-number") val phoneNumber: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
) : BaseDTO<UserEntity> {
    override fun toEntity(): UserEntity {
        return UserEntity(
            id = id,
            name = name,
            email = email,
            avatar = avatar,
            gender = gender,
            description = description,
            isFriend = isFriend,
            status = status,
            phoneNumber = phoneNumber,
            address = address,
            latitude = latitude,
            longitude = longitude,
        )
    }
}

package com.harian.closer.share.location.data.user.remote.dto

import com.google.gson.annotations.SerializedName

data class FriendsResponse (
    @SerializedName("friends") val friends: List<FriendDTO>,
    @SerializedName("count") val count: Int
)

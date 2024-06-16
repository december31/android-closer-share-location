package com.harian.closer.share.location.presentation.notification

data class NotificationModel(
    val type: Type,
    val title: String,
    val data: String
) {
    enum class Type {
        FRIEND_REQUEST,
        MESSAGE,
        POST
    }
}

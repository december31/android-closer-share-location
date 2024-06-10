package com.harian.closer.share.location.presentation.notification

data class NotificationData<T>(
    private val type: Type,
    private val data: T
) {
    enum class Type {
        FRIEND_REQUEST,
        MESSAGE
    }
}

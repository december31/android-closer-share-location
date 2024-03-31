package com.harian.closer.share.location.presentation.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FirebaseNotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(this.javaClass.simpleName, message.messageType.toString())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(this.javaClass.simpleName, "new token: $token")
    }
}

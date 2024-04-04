package com.harian.closer.share.location.presentation.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.harian.closer.share.location.domain.user.usecase.UpdateDeviceInformationUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseNotificationService : FirebaseMessagingService() {

    @Inject
    lateinit var updateDeviceInformationUseCase: UpdateDeviceInformationUseCase

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(this.javaClass.simpleName, message.messageType.toString())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(this.javaClass.simpleName, "new token: $token")
        CoroutineScope(Dispatchers.IO).launch {
            updateDeviceInformationUseCase.execute()
            cancel()
        }
    }
}

package com.harian.closer.share.location.presentation.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.harian.closer.share.location.domain.user.usecase.UpdateDeviceInformationUseCase
import com.harian.closer.share.location.utils.Constants
import com.harian.software.closer.share.location.R
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
        if (message.notification != null) {
            val notificationBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANEL_ID)
                    .setContentTitle(message.notification?.title ?: "")
                    .setContentText(message.notification?.body?.replace("\"", "") ?: "")
                    .setPriority(
                        message.notification?.notificationPriority
                            ?: NotificationCompat.PRIORITY_DEFAULT
                    )
                    .setStyle(NotificationCompat.BigTextStyle())
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.drawable.ic_launcher_foreground).setAutoCancel(true)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANEL_ID,
                Constants.NOTIFICATION_CHANEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
            notificationManager.notify(0, notificationBuilder.build())
        }
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

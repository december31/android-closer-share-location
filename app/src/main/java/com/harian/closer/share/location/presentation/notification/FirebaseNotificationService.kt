package com.harian.closer.share.location.presentation.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.harian.closer.share.location.MainActivity
import com.harian.closer.share.location.data.post.remote.dto.PostDTO
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

    private val id = 0

    companion object {
        const val KEY_NOTIFICATION_DATA = "notification_data"
        const val KEY_NOTIFICATION_TYPE = "notification_type"
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(this.javaClass.simpleName, message.messageType.toString())
        if (message.notification != null) {

            val body = message.notification?.body
            val notificationModel = Gson().fromJson(body, NotificationModel::class.java)

            val notificationBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANEL_ID)
                    .setContentTitle(message.notification?.title ?: "")
                    .setContentText(notificationModel.title)
                    .setPriority(
                        message.notification?.notificationPriority
                            ?: NotificationCompat.PRIORITY_DEFAULT
                    )
                    .setStyle(NotificationCompat.BigTextStyle())
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.mipmap.ic_launcher_foreground).setAutoCancel(true)
                    .setContentIntent(createPendingIntent(notificationModel))
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANEL_ID,
                Constants.NOTIFICATION_CHANEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
            notificationManager.notify(id, notificationBuilder.build())
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

    private fun createPendingIntent(model: NotificationModel): PendingIntent {
        val data = when (model.type) {
            NotificationModel.Type.FRIEND_REQUEST -> null
            NotificationModel.Type.MESSAGE -> null
            NotificationModel.Type.POST -> Gson().fromJson(model.data, PostDTO::class.java).toEntity()
        }
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtras(
            bundleOf(
                KEY_NOTIFICATION_DATA to data,
                KEY_NOTIFICATION_TYPE to model.type
            )
        )
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        return pendingIntent
    }
}

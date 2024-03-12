package com.harian.closer.share.location.data.messaging.repository

import com.harian.closer.share.location.domain.messaging.MessagingRepository
import com.harian.closer.share.location.platform.SharedPrefs
import com.harian.closer.share.location.utils.Constants
import com.harian.software.closer.share.location.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.sendText

class MessagingRepositoryImpl(private val stompClient: StompClient, private val sharedPrefs: SharedPrefs) : MessagingRepository {

    override fun listenForMessage(): Flow<String> {
        return flow<String> {
            val session = stompClient.connect(
                url = BuildConfig.WEB_SOCKET_END_POINT,
                customStompConnectHeaders = mapOf(
                    Constants.AUTHORIZATION to sharedPrefs.getToken()
                )
            )
            session.sendText("/app/greeting", "hello")
        }
    }
}

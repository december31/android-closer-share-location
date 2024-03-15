package com.harian.closer.share.location.data.messaging.repository

import com.harian.closer.share.location.domain.messaging.MessagingRepository
import com.harian.closer.share.location.platform.SharedPrefs
import com.harian.closer.share.location.utils.Constants
import com.harian.software.closer.share.location.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ua.naiksoftware.stomp.StompClient

class MessagingRepositoryImpl(private val stompClient: StompClient, private val sharedPrefs: SharedPrefs) : MessagingRepository {

    override fun listenForMessage(): Flow<String> {
        return flow<String> {
        }
    }
}

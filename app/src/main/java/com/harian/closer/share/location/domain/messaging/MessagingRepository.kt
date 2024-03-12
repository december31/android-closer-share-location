package com.harian.closer.share.location.domain.messaging

import kotlinx.coroutines.flow.Flow

interface MessagingRepository {
    fun listenForMessage(): Flow<String>
}

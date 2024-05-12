package com.harian.closer.share.location.domain.message.usecase

import com.harian.closer.share.location.domain.message.MessageRepository
import com.harian.closer.share.location.domain.message.entity.MessageEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubscribeMessageUseCase @Inject constructor(private val messageRepository: MessageRepository) {
    suspend fun execute(userEntity: UserEntity): Flow<MessageEntity?> {
        return messageRepository.subscribeMessage(userEntity)
    }
}

package com.harian.closer.share.location.domain.message.usecase

import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.message.remote.dto.MessageDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.message.MessageRepository
import com.harian.closer.share.location.domain.message.entity.MessageEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessageUseCase @Inject constructor(private val messageRepository: MessageRepository) {
    suspend fun execute(userEntity: UserEntity): Flow<BaseResult<List<MessageEntity>, WrappedListResponse<MessageDTO>>> {
        return messageRepository.getMessage(userEntity)
    }
}

package com.harian.closer.share.location.domain.message

import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.message.remote.dto.MessageDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.message.entity.MessageEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun subscribeMessage(userEntity: UserEntity): Flow<MessageEntity?>
    suspend fun sendMessage(messageEntity: MessageEntity): Flow<BaseResult<MessageEntity, WrappedResponse<MessageDTO>>>

    /**
     * get message between current logged in user and userEntity
     */
    suspend fun getMessage(userEntity: UserEntity): Flow<BaseResult<List<MessageEntity>, WrappedListResponse<MessageDTO>>>
}

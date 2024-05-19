package com.harian.closer.share.location.data.message.remote.dto

import com.google.gson.annotations.SerializedName
import com.harian.closer.share.location.data.common.base.BaseDTO
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import com.harian.closer.share.location.domain.message.entity.MessageEntity

data class MessageDTO(
    @SerializedName("receiver") val receiver: UserDTO?,
    @SerializedName("sender") val sender: UserDTO?,
    @SerializedName("message") val message: String?,
    @SerializedName("time") val time: Long?,
    @SerializedName("code") val code: Long?,
    @SerializedName("status") val status: MessageEntity.Status?,
    @SerializedName("type") val type: MessageEntity.Type?
) : BaseDTO<MessageEntity> {

    override fun toEntity(): MessageEntity {
        return MessageEntity(
            receiver = receiver?.toEntity(),
            sender = sender?.toEntity(),
            message = message,
            time = time,
            code = code,
            status = status,
            type = type
        )
    }
}

package com.harian.closer.share.location.domain.message.entity

import com.harian.closer.share.location.data.message.remote.dto.MessageDTO
import com.harian.closer.share.location.domain.common.base.BaseEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity

data class MessageEntity(
    val receiver: UserEntity?,
    val sender: UserEntity?,
    val message: String?,
    val time: Long?,
    val code: Long?,
    val status: Status?,
    val type: Type?
) : BaseEntity<MessageDTO> {
    override fun toDTO(): MessageDTO {
        return MessageDTO(
            receiver = receiver?.toDTO(),
            sender = sender?.toDTO(),
            message = message,
            time = time,
            code = code,
            status = status,
            type = type
        )
    }

    enum class Status {
        SENDING,
        SENT,
        RECEIVED,
        SEEN
    }

    enum class Type(val value: Int) {
        SEND(0),
        RECEIVE(1)
    }

}

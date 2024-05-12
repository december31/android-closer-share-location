package com.harian.closer.share.location.data.message.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.message.remote.api.MessageApi
import com.harian.closer.share.location.data.message.remote.dto.MessageDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.message.MessageRepository
import com.harian.closer.share.location.domain.message.entity.MessageEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent

class MessageRepositoryImpl(
    private val messageApi: MessageApi,
    private val stompClient: StompClient,
) : MessageRepository {

    private var bag = CompositeDisposable()
    private val flow: MutableStateFlow<MessageEntity?> = MutableStateFlow(null)

    override suspend fun subscribeMessage(userEntity: UserEntity): Flow<MessageEntity?> {
        initSessionIfNeed()
        bag.add(stompClient.topic("/topic/message/subscribe/${userEntity.id}")
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                { stompMessage ->
                    flow.value = Gson().fromJson(stompMessage.payload, MessageEntity::class.java)
                },
                {
                    Log.e(this.javaClass.simpleName, it.message.toString())
                }
            )
        )
        return flow
    }

    override suspend fun sendMessage(messageEntity: MessageEntity): Flow<BaseResult<MessageEntity, WrappedResponse<MessageDTO>>> {
        return flow {
            val response = messageApi.sendMessage(MessageDTO.fromMessageEntity(messageEntity))
            if (response.isSuccessful && response.code() in 200 until 400) {
                val message = response.body()?.data?.toEntity()
                emit(BaseResult.Success(message!!))
            } else {
                val type = object : TypeToken<WrappedResponse<MessageDTO>>() {}.type
                val err = Gson().fromJson<WrappedResponse<MessageDTO>>(response.errorBody()!!.charStream(), type)!!
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }

    override suspend fun getMessage(userEntity: UserEntity): Flow<BaseResult<List<MessageEntity>, WrappedListResponse<MessageDTO>>> {
        return flow {
            val response = messageApi.getMessages(userEntity.id!!)
            if (response.isSuccessful && response.code() in 200 until 400) {
                val data = response.body()?.data
                val messages = data?.map { it.toEntity() } ?: listOf()
                emit(BaseResult.Success(messages))
            } else {
                val type = object : TypeToken<WrappedListResponse<MessageDTO>>() {}.type
                val err = Gson().fromJson<WrappedListResponse<MessageDTO>>(response.errorBody()!!.charStream(), type)!!
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }

    private fun initSessionIfNeed() {
        if (stompClient.isConnected) return
        bag.add(stompClient.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                { lifeCycleEvent ->
                    when (lifeCycleEvent.type) {
                        LifecycleEvent.Type.OPENED -> Log.d(this.javaClass.simpleName, "connection established")
                        LifecycleEvent.Type.CLOSED -> Log.d(this.javaClass.simpleName, "connection closed")
                        LifecycleEvent.Type.ERROR -> Log.e(this.javaClass.simpleName, "error: ${lifeCycleEvent.exception.message}")
                        LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> Log.e(this.javaClass.simpleName, "failed server heartbeat")
                        null -> Unit
                    }
                },
                {
                    Log.e(this.javaClass.simpleName, "error: ${it.message}")
                }
            )
        )
        stompClient.connect()
    }

    fun disposeObserver() {
        bag.dispose()
        bag = CompositeDisposable()
    }
}

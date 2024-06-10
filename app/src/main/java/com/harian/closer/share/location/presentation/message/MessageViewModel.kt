package com.harian.closer.share.location.presentation.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.data.common.BaseViewModel
import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.message.remote.dto.MessageDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.message.entity.MessageEntity
import com.harian.closer.share.location.domain.message.usecase.GetMessageUseCase
import com.harian.closer.share.location.domain.message.usecase.SendMessageUseCase
import com.harian.closer.share.location.domain.message.usecase.SubscribeMessageUseCase
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.domain.user.usecase.GetUserInformationUseCase
import com.harian.closer.share.location.utils.extension.clone
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val getMessageUseCase: GetMessageUseCase,
    private val getUserInformationUseCase: GetUserInformationUseCase,
    private val subscribeMessageUseCase: SubscribeMessageUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow<ApiState>(ApiState.Init)
    val state: StateFlow<ApiState> get() = _state

    private val _messagesLiveData = MutableLiveData<List<MessageEntity>>()
    val messagesLiveData: LiveData<List<MessageEntity>> get() = _messagesLiveData
    fun subscribeMessage() {
        viewModelScope.launch {
            getUserInformationUseCase.execute()
                .catch {
                    logError(it.message.toString())
                }
                .collect {
                    when (it) {
                        is BaseResult.Success -> subscribeMessage(it.data)
                        is BaseResult.Error -> Unit
                    }
                }
        }
    }

    private fun subscribeMessage(user: UserEntity) {
        viewModelScope.launch {
            subscribeMessageUseCase.execute(user)
                .catch {
                    logError(it.message.toString())
                }
                .collect { messageEntity ->
                    messageEntity ?: return@collect
                    _messagesLiveData.postValue(messagesLiveData.value.orEmpty().clone().toMutableList().apply { add(0, messageEntity) }.distinct())
                }
        }
    }

    fun sendMessage(receiver: UserEntity, message: String) {
        viewModelScope.launch {
            getUserInformationUseCase.execute()
                .catch {
                    logError(it.message.toString())
                }
                .collect {
                    when (it) {
                        is BaseResult.Success -> sendMessage(it.data, receiver, message)
                        is BaseResult.Error -> Unit
                    }
                }
        }
    }

    private fun sendMessage(currentUser: UserEntity, receiver: UserEntity, message: String) {
        viewModelScope.launch {
            val messageEntity = MessageEntity(
                receiver = receiver,
                message = message.trim(),
                sender = currentUser,
                code = System.currentTimeMillis(),
                status = MessageEntity.Status.SENDING,
                type = MessageEntity.Type.SEND,
                time = null
            )
            sendMessageUseCase.execute(messageEntity)
                .catch {
                    logError(it.message.toString())
                }
                .collect {
                    when (it) {
                        is BaseResult.Success -> getMessage(receiver)
                        is BaseResult.Error -> Unit
                    }
                }
        }
    }

    fun getMessage(user: UserEntity) {
        viewModelScope.launch {
            getMessageUseCase.execute(user)
                .onStart {

                }
                .catch {
                    logError(it.message.toString())
                }
                .collect {
                    when (it) {
                        is BaseResult.Success -> {
                            _messagesLiveData.postValue(it.data)
                            _state.value = ApiState.SuccessGetMessages(it.data)
                        }

                        is BaseResult.Error -> _state.value = ApiState.ErrorGetMessages(it.rawResponse)
                    }
                }
        }
    }

    sealed class ApiState {
        data object Init : ApiState()
        data class SuccessGetMessages(val messages: List<MessageEntity>) : ApiState()
        data class ErrorGetMessages(val rawResponse: WrappedListResponse<MessageDTO>) : ApiState()
    }
}

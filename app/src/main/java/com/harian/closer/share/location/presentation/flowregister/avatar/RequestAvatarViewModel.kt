package com.harian.closer.share.location.presentation.flowregister.avatar

import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.data.common.BaseViewModel
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.domain.user.usecase.GetUserInformationUseCase
import com.harian.closer.share.location.domain.user.usecase.UpdateAvatarUseCase
import com.harian.closer.share.location.utils.runOnMainThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RequestAvatarViewModel @Inject constructor(
    private val getUserInformationUseCase: GetUserInformationUseCase,
    private val updateAvatarUseCase: UpdateAvatarUseCase
) : BaseViewModel() {
    private val _state = MutableStateFlow<RequestAvatarState>(RequestAvatarState.Init)
    val state = _state.asStateFlow()

    fun getUserInformation() {
        viewModelScope.launch(Dispatchers.IO) {
            getUserInformationUseCase.execute()
                .onStart {
                }
                .catch {
                    logError(it.message.toString())
                }
                .collect {
                    when (it) {
                        is BaseResult.Error -> _state.value = RequestAvatarState.FailedGetUserInformation(it.rawResponse)
                        is BaseResult.Success -> _state.value = RequestAvatarState.SuccessGetUserInformation(it.data)
                    }
                }
        }
    }

    fun updateAvatar(image: File?) {
        if (image == null) {
            _state.value = RequestAvatarState.FailedUpdateAvatar(null)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val body = image.asRequestBody()
            val part = MultipartBody.Part.createFormData("image", image.name, body)
            updateAvatarUseCase.execute(part)
                .onStart {
                    runOnMainThread {
                        _state.value = RequestAvatarState.Loading(true)
                    }
                }
                .catch {
                    logError(it.message.toString())
                    runOnMainThread {
                        _state.value = RequestAvatarState.Loading(false)
                    }
                }
                .collect {
                    runOnMainThread {
                        when (it) {
                            is BaseResult.Error -> _state.value = RequestAvatarState.FailedUpdateAvatar(it.rawResponse)
                            is BaseResult.Success -> _state.value = RequestAvatarState.SuccessUpdateAvatar(it.data)
                        }
                        _state.value = RequestAvatarState.Loading(false)
                    }
                }
        }

    }

    sealed class RequestAvatarState {
        data object Init : RequestAvatarState()
        data class Loading(val isLoading: Boolean) : RequestAvatarState()
        data class SuccessGetUserInformation(val data: UserEntity) : RequestAvatarState()
        data class FailedGetUserInformation(val rawResponse: WrappedResponse<UserDTO>) : RequestAvatarState()
        data class SuccessUpdateAvatar(val data: UserEntity) : RequestAvatarState()
        data class FailedUpdateAvatar(val rawResponse: WrappedResponse<UserDTO>?) : RequestAvatarState()
    }
}

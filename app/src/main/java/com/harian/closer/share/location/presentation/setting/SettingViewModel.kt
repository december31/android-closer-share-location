package com.harian.closer.share.location.presentation.setting

import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.data.common.BaseViewModel
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.domain.user.usecase.GetUserInformationUseCase
import com.harian.closer.share.location.domain.user.usecase.LogoutUseCase
import com.harian.closer.share.location.domain.user.usecase.UpdateAvatarUseCase
import com.harian.closer.share.location.platform.SharedPrefs
import com.harian.closer.share.location.utils.runOnMainThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getUserInformationUseCase: GetUserInformationUseCase,
    private val updateAvatarUseCase: UpdateAvatarUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val sharedPrefs: SharedPrefs
) : BaseViewModel() {
    private val _state = MutableStateFlow<SettingState>(SettingState.Init)
    val state: StateFlow<SettingState> = _state

    private val _updateAvatarState = MutableStateFlow<UpdateAvatarState>(UpdateAvatarState.Init)
    val updateAvatarState: StateFlow<UpdateAvatarState> = _updateAvatarState

    fun fetchUserInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            getUserInformationUseCase.execute()
                .onStart { showLoading() }
                .catch {
                    it.printStackTrace()
                    hideLoading()
                }
                .collect {
                    runOnMainThread {
                        when (it) {
                            is BaseResult.Success -> _state.value = SettingState.SuccessGetUserInformation(it.data)
                            is BaseResult.Error -> _state.value = SettingState.ErrorGetUserInformation(it.rawResponse)
                        }
                    }
                    hideLoading()
                }
        }
    }

    fun updateAvatar(image: File?) {
        if (image == null) {
            _updateAvatarState.value = UpdateAvatarState.FailedUpdateAvatar(null)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val body = image.asRequestBody()
            val part = MultipartBody.Part.createFormData("image", image.name, body)
            updateAvatarUseCase.execute(part)
                .onStart {
                    showLoading()
                }
                .catch {
                    logError(it.message.toString())
                    hideLoading()
                }
                .collect {
                    runOnMainThread {
                        when (it) {
                            is BaseResult.Error -> _updateAvatarState.value = UpdateAvatarState.FailedUpdateAvatar(it.rawResponse)
                            is BaseResult.Success -> _updateAvatarState.value = UpdateAvatarState.SuccessUpdateAvatar(it.data)
                        }
                    }
                    hideLoading()
                }
        }

    }

    fun logout() {
        sharedPrefs.clearTokens()
        _state.value = SettingState.SuccessLogout

        viewModelScope.launch(Dispatchers.IO) {
            logoutUseCase.execute()
        }
    }

    sealed class SettingState {
        data object Init : SettingState()
        data object SuccessLogout : SettingState()
        data object ErrorLogout : SettingState()
        data class ErrorGetUserInformation(val rawResponse: WrappedResponse<UserDTO>?) : SettingState()
        data class SuccessGetUserInformation(val user: UserEntity) : SettingState()
    }

    sealed class UpdateAvatarState {
        data object Init : UpdateAvatarState()
        data class SuccessUpdateAvatar(val data: UserEntity) : UpdateAvatarState()
        data class FailedUpdateAvatar(val rawResponse: WrappedResponse<UserDTO>?) : UpdateAvatarState()
    }
}

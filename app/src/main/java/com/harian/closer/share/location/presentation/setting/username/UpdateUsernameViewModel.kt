package com.harian.closer.share.location.presentation.setting.username

import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.platform.BaseViewModel
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.domain.user.usecase.GetUserInformationUseCase
import com.harian.closer.share.location.domain.user.usecase.UpdateUsernameUseCase
import com.harian.closer.share.location.utils.runOnMainThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateUsernameViewModel @Inject constructor(
    private val updateUsernameUseCase: UpdateUsernameUseCase,
    private val getUserInformationUseCase: GetUserInformationUseCase
) : BaseViewModel() {
    private val _updateUsernameState = MutableStateFlow<UpdateUsernameState>(UpdateUsernameState.Init)
    val updateUsernameState: StateFlow<UpdateUsernameState> = _updateUsernameState

    private val _getUserInformationState = MutableStateFlow<GetUserInformationState>(GetUserInformationState.Init)
    val getUserInformationState: StateFlow<GetUserInformationState> = _getUserInformationState

    fun fetchUserInformation() {
        viewModelScope.launch(Dispatchers.IO) {
            getUserInformationUseCase.execute()
                .onStart { showLoading() }
                .catch {
                    it.printStackTrace()
                    hideLoading()
                }
                .collect {
                    hideLoading()
                    runOnMainThread {
                        when (it) {
                            is BaseResult.Error -> _getUserInformationState.value = GetUserInformationState.Error(it.rawResponse)
                            is BaseResult.Success -> _getUserInformationState.value = GetUserInformationState.Success(it.data)
                        }
                    }
                }
        }
    }

    fun updateUsername(username: String?) {
        username ?: return
        viewModelScope.launch(Dispatchers.IO) {
            updateUsernameUseCase.execute(username)
                .onStart { showLoading() }
                .catch {
                    it.printStackTrace()
                    hideLoading()
                }
                .collect {
                    hideLoading()
                    runOnMainThread {
                        when (it) {
                            is BaseResult.Error -> _updateUsernameState.value = UpdateUsernameState.Error(it.rawResponse)
                            is BaseResult.Success -> _updateUsernameState.value = UpdateUsernameState.Success(it.data)
                        }
                    }
                }
        }
    }

    sealed class UpdateUsernameState {
        data object Init : UpdateUsernameState()
        data class Success(val data: UserEntity) : UpdateUsernameState()
        data class Error(val rawResponse: WrappedResponse<UserDTO>) : UpdateUsernameState()
    }

    sealed class GetUserInformationState {
        data object Init : GetUserInformationState()
        data class Success(val data: UserEntity) : GetUserInformationState()
        data class Error(val rawResponse: WrappedResponse<UserDTO>) : GetUserInformationState()
    }
}

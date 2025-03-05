package com.harian.closer.share.location.presentation.flowregister.phonenumber

import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.platform.BaseViewModel
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.domain.user.usecase.GetUserInformationUseCase
import com.harian.closer.share.location.domain.user.usecase.UpdateUserInformationUseCase
import com.harian.closer.share.location.utils.runOnMainThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RequestPhoneNumberViewModel @Inject constructor(
    private val updateUserInformationUseCase: UpdateUserInformationUseCase,
    private val getUserInformationUseCase: GetUserInformationUseCase
) : BaseViewModel() {
    private val _state = MutableStateFlow<RequestPhoneNumberState>(RequestPhoneNumberState.Init)
    val state: StateFlow<RequestPhoneNumberState> = _state

    fun updateUserPhoneNumber(phoneNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getUserInformationUseCase.execute()
                .onStart {

                }
                .collect {
                    when (it) {
                        is BaseResult.Error -> _state.value = RequestPhoneNumberState.Error(it.rawResponse)
                        is BaseResult.Success -> updateUserPhoneNumber(it.data.apply { this.phoneNumber = phoneNumber })
                    }
                }
        }
    }

    private suspend fun updateUserPhoneNumber(userEntity: UserEntity) {
        withContext(Dispatchers.IO) {
            updateUserInformationUseCase.execute(userEntity)
                .onStart {
                    runOnMainThread {
                        _state.value = RequestPhoneNumberState.Loading(true)
                    }
                }
                .catch {
                    runOnMainThread {
                        _state.value = RequestPhoneNumberState.Loading(false)
                    }
                }
                .collect {
                    runOnMainThread {
                        when (it) {
                            is BaseResult.Error -> _state.value = RequestPhoneNumberState.Error(it.rawResponse)
                            is BaseResult.Success -> _state.value = RequestPhoneNumberState.Success(it.data)
                        }
                        _state.value = RequestPhoneNumberState.Loading(false)
                    }
                }
        }
    }

    sealed class RequestPhoneNumberState {
        data object Init : RequestPhoneNumberState()
        data class Loading(val isLoading: Boolean) : RequestPhoneNumberState()
        data class Success(val data: UserEntity) : RequestPhoneNumberState()
        data class Error(val rawResponse: WrappedResponse<UserDTO>) : RequestPhoneNumberState()
    }
}

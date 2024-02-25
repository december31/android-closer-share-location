package com.harian.closer.share.location.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.user.remote.dto.UserResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.domain.user.usecase.GetUserInformationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUserInformationUseCase: GetUserInformationUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<FunctionState>(FunctionState.Init)
    val state: StateFlow<FunctionState> = _state

    fun fetchUserInformation() {
        viewModelScope.launch {
            getUserInformationUseCase.execute()
                .onStart {

                }
                .catch {
                    it.printStackTrace()
                    _state.value = FunctionState.ErrorGetUserInfo(null)
                }
                .collect { baseResult ->
                    when (baseResult) {
                        is BaseResult.Success -> _state.value = FunctionState.SuccessGetUserInfo(baseResult.data)
                        is BaseResult.Error -> _state.value = FunctionState.ErrorGetUserInfo(baseResult.rawResponse)
                    }
                }
        }
    }

    sealed class FunctionState {
        data object Init : FunctionState()
        data class IsLoading(val isLoading: Boolean) : FunctionState()
        data class SuccessGetUserInfo(val userEntity: UserEntity) : FunctionState()
        data class ErrorGetUserInfo(val rawResponse: WrappedResponse<UserResponse>?) : FunctionState()
    }
}

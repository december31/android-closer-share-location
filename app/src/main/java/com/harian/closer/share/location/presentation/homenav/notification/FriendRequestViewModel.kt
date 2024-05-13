package com.harian.closer.share.location.presentation.homenav.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.user.remote.dto.FriendRequestDTO
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.user.entity.FriendRequestEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.domain.user.usecase.AcceptFriendRequestUseCase
import com.harian.closer.share.location.domain.user.usecase.DenyFriendRequestUseCase
import com.harian.closer.share.location.domain.user.usecase.GetFriendRequestsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendRequestViewModel @Inject constructor(
    private val getFriendRequestUseCase: GetFriendRequestsUseCase,
    private val acceptFriendRequestUseCase: AcceptFriendRequestUseCase,
    private val denyFriendRequestUseCase: DenyFriendRequestUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<ApiState>(ApiState.Init)
    val state: StateFlow<ApiState> get() = _state

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Init)
    val loadingState: StateFlow<LoadingState> get() = _loadingState

    fun fetchFriendRequest() {
        viewModelScope.launch {
            getFriendRequestUseCase.execute()
                .catch {
                    it.printStackTrace()
                }
                .collect { baseResult ->
                    when (baseResult) {
                        is BaseResult.Success -> _state.value = ApiState.SuccessGetFriendRequest(baseResult.data)
                        is BaseResult.Error -> _state.value = ApiState.ErrorGetFriendRequest(baseResult.rawResponse)
                    }
                }
        }
    }

    fun acceptFriendRequest(user: UserEntity) {
        viewModelScope.launch {
            acceptFriendRequestUseCase.execute(user)
                .onStart {
                    _loadingState.emit(LoadingState.StartLoading(user))
                }
                .onCompletion {
                    _loadingState.emit(LoadingState.CancelLoading(user))
                }
                .collect { baseResult ->
                    when (baseResult) {
                        is BaseResult.Error -> _state.emit(ApiState.ErrorAcceptFriendRequest(baseResult.rawResponse))
                        is BaseResult.Success -> _state.emit(ApiState.SuccessAcceptFriendRequest(user))
                    }
                }
        }
    }

    fun denyFriendRequest(user: UserEntity) {
        viewModelScope.launch {
            denyFriendRequestUseCase.execute(user)
                .onStart {
                    _loadingState.emit(LoadingState.StartLoading(user))
                }
                .onCompletion {
                    _loadingState.emit(LoadingState.CancelLoading(user))
                }
                .collect {
                    when(it) {
                        is BaseResult.Error -> _state.emit(ApiState.ErrorDenyFriendRequest(it.rawResponse))
                        is BaseResult.Success -> _state.emit(ApiState.SuccessDenyFriendRequest(user))
                    }
                }
        }
    }

    sealed class ApiState {
        data object Init : ApiState()
        data class SuccessGetFriendRequest(val friendRequests: List<FriendRequestEntity>) : ApiState()
        data class ErrorGetFriendRequest(val rawResponse: WrappedListResponse<FriendRequestDTO>) : ApiState()
        data class SuccessAcceptFriendRequest(val user: UserEntity) : ApiState()
        data class ErrorAcceptFriendRequest(val rawResponse: WrappedResponse<UserDTO>) : ApiState()
        data class SuccessDenyFriendRequest(val user: UserEntity) : ApiState()
        data class ErrorDenyFriendRequest(val rawResponse: WrappedResponse<UserDTO>) : ApiState()
    }

    sealed class LoadingState {
        data object Init : LoadingState()
        data class CancelLoading(val user: UserEntity) : LoadingState()
        data class StartLoading(val user: UserEntity) : LoadingState()
    }
}

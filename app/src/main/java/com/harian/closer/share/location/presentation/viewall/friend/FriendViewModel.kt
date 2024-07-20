package com.harian.closer.share.location.presentation.viewall.friend

import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.platform.BaseViewModel
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.user.remote.dto.FriendsResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.user.entity.FriendEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.domain.user.usecase.GetFriendsUseCase
import com.harian.closer.share.location.domain.user.usecase.GetUserInformationUseCase
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
class FriendViewModel @Inject constructor(
    private val getFriendsUseCase: GetFriendsUseCase,
    private val getUserInformationUseCase: GetUserInformationUseCase
) : BaseViewModel() {
    private val _friendsStates = MutableStateFlow<ApiState>(ApiState.Init)
    val friendsStates: StateFlow<ApiState> get() = _friendsStates

    private val _userInformationStates = MutableStateFlow<ApiState>(ApiState.Init)
    val userInformationStates: StateFlow<ApiState> get() = _userInformationStates

    fun fetchFriends(user: UserEntity?) {
        viewModelScope.launch(Dispatchers.IO) {
            getUserInformationUseCase.execute(user)
                .onStart { showLoading() }
                .catch { hideLoading() }
                .collect { result ->
                    (result as? BaseResult.Success)?.data?.let {
                        runOnMainThread {
                            _userInformationStates.value = ApiState.SuccessGetUserInformation(it)
                        }

                        getFriendsUseCase.execute(it, 0, 100)
                            .catch { hideLoading() }
                            .collect {
                                hideLoading()
                                runOnMainThread {
                                    when (it) {
                                        is BaseResult.Error -> {
                                            _friendsStates.value = ApiState.ErrorGetFriends(it.rawResponse)
                                        }

                                        is BaseResult.Success -> {
                                            _friendsStates.value = ApiState.SuccessGetFriends(it.data.friends)
                                        }
                                    }
                                }
                            }
                    }
                }
        }
    }

    sealed class ApiState {
        data object Init : ApiState()
        data class SuccessGetFriends(val friends: List<FriendEntity>) : ApiState()
        data class ErrorGetFriends(val rawResponse: WrappedResponse<FriendsResponse>) : ApiState()
        data class SuccessGetUserInformation(val user: UserEntity) : ApiState()
        data class ErrorGetUserInformation(val rawResponse: WrappedResponse<UserEntity>) : ApiState()
    }
}

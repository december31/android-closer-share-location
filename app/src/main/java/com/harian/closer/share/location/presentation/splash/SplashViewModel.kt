package com.harian.closer.share.location.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.login.usecase.TokenAuthenticateUseCase
import com.harian.closer.share.location.domain.user.UserRepository
import com.harian.closer.share.location.domain.user.usecase.GetUserInformationUseCase
import com.harian.closer.share.location.domain.user.usecase.UpdateDeviceInformationUseCase
import com.harian.closer.share.location.platform.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenAuthenticateUseCase: TokenAuthenticateUseCase,
    private val updateDeviceInformationUseCase: UpdateDeviceInformationUseCase,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {
    private val _state = MutableStateFlow<FunctionState>(FunctionState.Init)
    val state: StateFlow<FunctionState> = _state

    fun verifyToken() {

        viewModelScope.launch {
            delay(2000)
            tokenAuthenticateUseCase.execute()
                .onStart {

                }
                .catch {
                    it.printStackTrace()
                    _state.value = FunctionState.ErrorVerifyToken
                }
                .collect { baseResult ->
                    when (baseResult) {
                        is BaseResult.Success -> {
                            if (sharedPrefs.needResetPassword) {
                                _state.value = FunctionState.NeedResetPassword
                            } else {
                                _state.value = FunctionState.SuccessVerifyToken
                                updateDeviceInformationUseCase.execute()
                            }
                        }

                        is BaseResult.Error -> _state.value = FunctionState.ErrorVerifyToken
                    }
                }
        }
    }

    fun resetState() {
        _state.value = FunctionState.Init
    }

    sealed class FunctionState {
        data object Init : FunctionState()
        data object SuccessVerifyToken : FunctionState()
        data object ErrorVerifyToken : FunctionState()
        data object NeedResetPassword : FunctionState()
    }
}

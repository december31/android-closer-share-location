package com.harian.closer.share.location.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.login.remote.dto.LoginRequest
import com.harian.closer.share.location.data.login.remote.dto.LoginResponse
import com.harian.closer.share.location.data.refresh.token.remote.dto.RefreshTokenResponse
import com.harian.closer.share.location.data.register.remote.dto.RegisterRequest
import com.harian.closer.share.location.data.register.remote.dto.RegisterResponse
import com.harian.closer.share.location.data.request.otp.remote.dto.RequestOtpRequest
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.login.entity.LoginEntity
import com.harian.closer.share.location.domain.login.usecase.LoginUseCase
import com.harian.closer.share.location.domain.refresh.token.entity.RefreshTokenEntity
import com.harian.closer.share.location.domain.refresh.token.usecase.RefreshTokenUseCase
import com.harian.closer.share.location.domain.register.entity.RegisterEntity
import com.harian.closer.share.location.domain.register.usecase.RegisterUseCase
import com.harian.closer.share.location.domain.request.api.usecase.RequestOtpUseCase
import com.harian.closer.share.location.platform.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val requestOtpUseCase: RequestOtpUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    private val _state = MutableStateFlow<FunctionState>(FunctionState.Init)
    val state: StateFlow<FunctionState> = _state

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            loginUseCase.execute(loginRequest)
                .onStart {
                    showLoading()
                }
                .catch {
                    it.printStackTrace()
                    hideLoading()
                }
                .collect { baseResult ->
                    hideLoading()
                    when (baseResult) {
                        is BaseResult.Error -> _state.emit(FunctionState.ErrorLogin(baseResult.rawResponse))
                        is BaseResult.Success -> {
                            sharedPrefs.saveToken(baseResult.data.token)
                            _state.emit(FunctionState.SuccessLogin(baseResult.data))
                        }
                    }
                }
        }
    }

    fun register(registerRequest: RegisterRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            registerUseCase.execute(registerRequest)
                .onStart {
                    showLoading()
                }
                .catch {
                    it.printStackTrace()
                    hideLoading()
                }
                .collect { baseResult ->
                    hideLoading()
                    when (baseResult) {
                        is BaseResult.Error -> _state.emit(FunctionState.ErrorRegister(baseResult.rawResponse))
                        is BaseResult.Success -> {
                            sharedPrefs.saveToken(baseResult.data.token)
                            _state.emit(FunctionState.SuccessRegister(baseResult.data))
                        }
                    }
                }
        }
    }

    fun requestOtp(requestOtpRequest: RequestOtpRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            requestOtpUseCase.execute(requestOtpRequest)
                .onStart {
                    showLoading()
                }
                .catch {
                    it.printStackTrace()
                    hideLoading()
                }
                .collect { baseResult ->
                    hideLoading()
                    when (baseResult) {
                        is BaseResult.Error -> _state.emit(FunctionState.ErrorRequestOtp(baseResult.rawResponse))
                        is BaseResult.Success -> _state.emit(FunctionState.SuccessRequestOtp)
                    }
                }
        }
    }

    fun refreshToken() {
        viewModelScope.launch(Dispatchers.IO) {
            refreshTokenUseCase.execute()
                .onStart {

                }
                .catch {
                    it.printStackTrace()
                    _state.emit(FunctionState.ErrorRefreshToken(null))
                }
                .collect { baseResult ->
                    when (baseResult) {
                        is BaseResult.Error -> _state.emit(FunctionState.ErrorRefreshToken(baseResult.rawResponse))
                        is BaseResult.Success -> {
                            sharedPrefs.saveToken(baseResult.data.token)
                            _state.emit(FunctionState.SuccessRefreshToken(baseResult.data))
                        }
                    }
                }
        }
    }

    private suspend fun showLoading() {
        _state.emit(FunctionState.IsLoading(true))
    }

    private suspend fun hideLoading() {
        _state.emit(FunctionState.IsLoading(false))
    }

    sealed class FunctionState {
        data object Init : FunctionState()
        data class IsLoading(val isLoading: Boolean) : FunctionState()
        data class SuccessLogin(val loginEntity: LoginEntity) : FunctionState()
        data class ErrorLogin(val rawResponse: WrappedResponse<LoginResponse>) : FunctionState()
        data class SuccessRegister(val loginEntity: RegisterEntity) : FunctionState()
        data class ErrorRegister(val rawResponse: WrappedResponse<RegisterResponse>) : FunctionState()
        data object SuccessRequestOtp : FunctionState()
        data class ErrorRequestOtp(val rawResponse: WrappedResponse<Unit>) : FunctionState()
        data class SuccessRefreshToken(val refreshTokenEntity: RefreshTokenEntity) : FunctionState()
        data class ErrorRefreshToken(val rawResponse: WrappedResponse<RefreshTokenResponse>?) : FunctionState()
    }
}

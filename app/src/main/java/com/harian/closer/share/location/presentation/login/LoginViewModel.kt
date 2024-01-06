package com.harian.closer.share.location.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.data.authenticate.remote.dto.AuthenticateRequest
import com.harian.closer.share.location.data.authenticate.remote.dto.AuthenticateResponse
import com.harian.closer.share.location.data.authenticate.remote.dto.OtpAuthenticateRequest
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.register.remote.dto.RegisterRequest
import com.harian.closer.share.location.data.register.remote.dto.RegisterResponse
import com.harian.closer.share.location.data.request.otp.remote.dto.RequestOtpRequest
import com.harian.closer.share.location.data.resetpassword.remote.dto.ResetPasswordRequest
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.login.entity.AuthenticateEntity
import com.harian.closer.share.location.domain.login.usecase.AuthenticateUseCase
import com.harian.closer.share.location.domain.login.usecase.OtpAuthenticateUseCase
import com.harian.closer.share.location.domain.register.entity.RegisterEntity
import com.harian.closer.share.location.domain.register.usecase.RegisterUseCase
import com.harian.closer.share.location.domain.request.otp.usecase.RequestOtpUseCase
import com.harian.closer.share.location.domain.resetpassword.usecase.ResetPasswordUseCase
import com.harian.closer.share.location.platform.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authenticateUseCase: AuthenticateUseCase,
    private val registerUseCase: RegisterUseCase,
    private val requestOtpUseCase: RequestOtpUseCase,
    private val otpAuthenticateUseCase: OtpAuthenticateUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    private val _state = MutableStateFlow<FunctionState>(FunctionState.Init)
    val state: StateFlow<FunctionState> = _state

    fun authenticate(authenticateRequest: AuthenticateRequest) {
        viewModelScope.launch {
            authenticateUseCase.execute(authenticateRequest)
                .onStart {
                    showLoading()
                }
                .catch {
                    it.printStackTrace()
                    hideLoading()
                    _state.value = FunctionState.ErrorLogin()
                }
                .collect { baseResult ->
                    hideLoading()
                    when (baseResult) {
                        is BaseResult.Error -> _state.value = FunctionState.ErrorLogin(baseResult.rawResponse)
                        is BaseResult.Success -> {
                            sharedPrefs.saveToken(baseResult.data.token)
                            _state.value = FunctionState.SuccessLogin(baseResult.data)
                        }
                    }
                }
        }
    }

    fun authenticate(otpAuthenticateRequest: OtpAuthenticateRequest) {
        viewModelScope.launch {
            otpAuthenticateUseCase.execute(otpAuthenticateRequest)
                .onStart {
                    showLoading()
                }
                .catch {
                    it.printStackTrace()
                    hideLoading()
                    _state.value = FunctionState.ErrorOtpAuthenticate(null)
                }
                .collect { baseResult ->
                    hideLoading()
                    when (baseResult) {
                        is BaseResult.Error -> _state.value = FunctionState.ErrorOtpAuthenticate(baseResult.rawResponse)
                        is BaseResult.Success -> {
                            sharedPrefs.saveToken(baseResult.data.token)
                            sharedPrefs.needResetPassword = true
                            _state.value = FunctionState.SuccessOtpAuthenticate(baseResult.data)
                        }
                    }
                }
        }
    }

    fun register(registerRequest: RegisterRequest) {
        viewModelScope.launch {
            registerUseCase.execute(registerRequest)
                .onStart {
                    showLoading()
                }
                .catch {
                    it.printStackTrace()
                    hideLoading()
                    _state.value = FunctionState.ErrorRegister(null)
                }
                .collect { baseResult ->
                    hideLoading()
                    when (baseResult) {
                        is BaseResult.Error -> _state.value = FunctionState.ErrorRegister(baseResult.rawResponse)
                        is BaseResult.Success -> {
                            sharedPrefs.saveToken(baseResult.data.token)
                            _state.value = FunctionState.SuccessRegister(baseResult.data)
                        }
                    }
                }
        }
    }

    fun requestOtpForRegister(requestOtpRequest: RequestOtpRequest) {
        viewModelScope.launch {
            requestOtpUseCase.execute(requestOtpRequest)
                .onStart {
                    showLoading()
                }
                .catch {
                    it.printStackTrace()
                    hideLoading()
                    _state.value = FunctionState.ErrorRequestOtpForRegister(null)
                }
                .collect { baseResult ->
                    hideLoading()
                    when (baseResult) {
                        is BaseResult.Error -> _state.value = FunctionState.ErrorRequestOtpForRegister(baseResult.rawResponse)
                        is BaseResult.Success -> _state.value = FunctionState.SuccessRequestOtpForRegister
                    }
                }
        }
    }

    fun requestOtpForResetPassword(requestOtpRequest: RequestOtpRequest) {
        viewModelScope.launch {
            requestOtpUseCase.execute(requestOtpRequest)
                .onStart {
                    showLoading()
                }
                .catch {
                    it.printStackTrace()
                    hideLoading()
                    _state.value = FunctionState.ErrorRequestOtpForResetPassword(null)
                }
                .collect { baseResult ->
                    hideLoading()
                    when (baseResult) {
                        is BaseResult.Error -> _state.value =
                            FunctionState.ErrorRequestOtpForResetPassword(baseResult.rawResponse)

                        is BaseResult.Success -> _state.value = FunctionState.SuccessRequestOtpForResetPassword
                    }
                }
        }
    }

    fun resetPassword(resetPasswordRequest: ResetPasswordRequest) {
        viewModelScope.launch {
            resetPasswordUseCase.execute(resetPasswordRequest)
                .onStart {
                    showLoading()
                }
                .catch {
                    it.printStackTrace()
                    hideLoading()
                    _state.value = FunctionState.ErrorResetPassword(null)
                }
                .collect { baseResult ->
                    hideLoading()
                    when (baseResult) {
                        is BaseResult.Error -> _state.value = FunctionState.ErrorResetPassword(baseResult.rawResponse)
                        is BaseResult.Success -> {
                            _state.value = FunctionState.SuccessResetPassword
                            sharedPrefs.needResetPassword = false
                        }
                    }
                }
        }
    }

    private fun showLoading() {
        _state.value = FunctionState.IsLoading(true)
    }

    private fun hideLoading() {
        _state.value = FunctionState.IsLoading(false)
    }

    sealed class FunctionState {
        data object Init : FunctionState()
        data class IsLoading(val isLoading: Boolean) : FunctionState()
        data class SuccessLogin(val authenticateEntity: AuthenticateEntity) : FunctionState()
        data class ErrorLogin(val rawResponse: WrappedResponse<AuthenticateResponse>? = null) : FunctionState()
        data class SuccessRegister(val loginEntity: RegisterEntity) : FunctionState()
        data class ErrorRegister(val rawResponse: WrappedResponse<RegisterResponse>? = null) : FunctionState()
        data object SuccessRequestOtpForRegister : FunctionState()
        data class ErrorRequestOtpForRegister(val rawResponse: WrappedResponse<Unit>? = null) : FunctionState()
        data object SuccessResetPassword : FunctionState()
        data class ErrorResetPassword(val rawResponse: WrappedResponse<Unit>? = null) : FunctionState()
        data object SuccessRequestOtpForResetPassword : FunctionState()
        data class ErrorRequestOtpForResetPassword(val rawResponse: WrappedResponse<Unit>? = null) : FunctionState()
        data class SuccessOtpAuthenticate(val loginEntity: AuthenticateEntity) : FunctionState()
        data class ErrorOtpAuthenticate(val rawResponse: WrappedResponse<AuthenticateResponse>? = null) : FunctionState()
    }
}

package com.harian.closer.share.location.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.login.remote.dto.LoginRequest
import com.harian.closer.share.location.data.login.remote.dto.LoginResponse
import com.harian.closer.share.location.data.register.remote.dto.RegisterRequest
import com.harian.closer.share.location.data.register.remote.dto.RegisterResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.login.entity.LoginEntity
import com.harian.closer.share.location.domain.login.usecase.LoginUseCase
import com.harian.closer.share.location.domain.register.entity.RegisterEntity
import com.harian.closer.share.location.domain.register.usecase.RegisterUseCase
import com.harian.closer.share.location.platform.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    private val _state = MutableStateFlow<UIState>(UIState.Init)
    val state: MutableStateFlow<UIState> = _state

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            loginUseCase.execute(loginRequest)
                .onStart {
                    showLoading()
                }
                .catch {
                    hideLoading()
                }
                .collect { baseResult ->
                    hideLoading()
                    when (baseResult) {
                        is BaseResult.Error -> state.value = UIState.ErrorLogin(baseResult.rawResponse)
                        is BaseResult.Success -> state.value = UIState.SuccessLogin(baseResult.data)
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
                    hideLoading()
                }
                .collect { baseResult ->
                    hideLoading()
                    when (baseResult) {
                        is BaseResult.Error -> state.value = UIState.ErrorRegister(baseResult.rawResponse)
                        is BaseResult.Success -> state.value = UIState.SuccessRegister(baseResult.data)
                    }
                }
        }
    }

    private fun showLoading() {
        _state.value = UIState.IsLoading(true)
    }

    private fun hideLoading() {
        _state.value = UIState.IsLoading(false)
    }

    sealed class UIState {
        data object Init : UIState()
        data class IsLoading(val isLoading: Boolean) : UIState()
        data class SuccessLogin(val loginEntity: LoginEntity) : UIState()
        data class ErrorLogin(val rawResponse: WrappedResponse<LoginResponse>) : UIState()
        data class SuccessRegister(val loginEntity: RegisterEntity) : UIState()
        data class ErrorRegister(val rawResponse: WrappedResponse<RegisterResponse>) : UIState()
    }

}

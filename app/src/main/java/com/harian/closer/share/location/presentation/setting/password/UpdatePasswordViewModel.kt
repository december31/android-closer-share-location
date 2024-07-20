package com.harian.closer.share.location.presentation.setting.password

import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.platform.BaseViewModel
import com.harian.closer.share.location.data.user.remote.dto.UpdatePasswordRequest
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.user.usecase.UpdatePasswordUseCase
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
class UpdatePasswordViewModel @Inject constructor(
    private val updatePasswordUseCase: UpdatePasswordUseCase
) : BaseViewModel() {
    private val _updatePasswordState = MutableStateFlow<UpdatePasswordState>(UpdatePasswordState.Init)
    val updatePasswordState: StateFlow<UpdatePasswordState> = _updatePasswordState

    fun updatePassword(request: UpdatePasswordRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            updatePasswordUseCase.execute(request)
                .onStart { showLoading() }
                .catch {
                    it.printStackTrace()
                    hideLoading()
                }
                .collect {
                    hideLoading()
                    runOnMainThread {
                        _updatePasswordState.value = when (it) {
                            is BaseResult.Success -> UpdatePasswordState.Success
                            is BaseResult.Error -> UpdatePasswordState.Error
                        }
                    }
                }
        }
    }

    sealed class UpdatePasswordState {
        data object Init : UpdatePasswordState()
        data object Success : UpdatePasswordState()
        data object Error : UpdatePasswordState()
    }
}

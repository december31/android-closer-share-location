package com.harian.closer.share.location.presentation.addfriend

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.platform.BaseViewModel
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.domain.user.usecase.GetUserInformationUseCase
import com.harian.software.closer.share.location.R
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
class ScanQrCodeViewModel @Inject constructor(
    private val getUserInformationUseCase: GetUserInformationUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow<GetUserInformationState>(GetUserInformationState.Init)
    val state: StateFlow<GetUserInformationState> = _state

    fun processQrCodeResult(userId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = try {
                userId?.toLong()
            } catch (e: Exception) {
                null
            }
            id ?: return@launch
            getUserInformationUseCase.execute(id)
                .onStart {
                    withContext(Dispatchers.Main) {
                        _state.value = GetUserInformationState.Loading(true)
                    }
                }
                .catch {
                    withContext(Dispatchers.Main) {
                        _state.value = GetUserInformationState.Loading(false)
                        _state.value = GetUserInformationState.Error(R.string.error_get_user_information)
                    }
                }
                .collect {
                    withContext(Dispatchers.Main) {
                        when (it) {
                            is BaseResult.Error -> _state.value = GetUserInformationState.Error(R.string.error_get_user_information)
                            is BaseResult.Success -> _state.value = GetUserInformationState.Success(it.data)
                        }
                        _state.value = GetUserInformationState.Loading(false)
                    }
                }
        }
    }

    fun resetState() {
        _state.value = GetUserInformationState.Init
    }

    sealed class GetUserInformationState {
        data object Init : GetUserInformationState()
        data class Loading(val isLoading: Boolean) : GetUserInformationState()
        data class Success(val user: UserEntity) : GetUserInformationState()
        data class Error(@StringRes val message: Int) : GetUserInformationState()
    }
}

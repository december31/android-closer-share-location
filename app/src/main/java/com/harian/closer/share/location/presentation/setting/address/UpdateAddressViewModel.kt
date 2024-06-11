package com.harian.closer.share.location.presentation.setting.address

import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.data.common.BaseViewModel
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.domain.user.usecase.ClearUserDataCacheUseCase
import com.harian.closer.share.location.domain.user.usecase.UpdateAddressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateAddressViewModel @Inject constructor(
    private val updateAddressUseCase: UpdateAddressUseCase,
    private val clearUserDataCacheUseCase: ClearUserDataCacheUseCase
) : BaseViewModel() {
    private val _updateAddressState = MutableStateFlow<UpdateAddressState>(UpdateAddressState.Init)
    val updateAddressState: StateFlow<UpdateAddressState> = _updateAddressState

    fun updateAddress(address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            updateAddressUseCase.execute(address)
                .onStart {
                    showLoading()
                    clearUserDataCacheUseCase.execute()
                }
                .catch {
                    it.printStackTrace()
                    hideLoading()
                    _updateAddressState.value = UpdateAddressState.Error(null)
                }
                .collect {
                    hideLoading()
                    when (it) {
                        is BaseResult.Error -> _updateAddressState.value = UpdateAddressState.Error(it.rawResponse)
                        is BaseResult.Success -> _updateAddressState.value = UpdateAddressState.Success(it.data)
                    }
                }
        }
    }

    sealed class UpdateAddressState {
        data object Init : UpdateAddressState()
        data class Success(val data: UserEntity) : UpdateAddressState()
        data class Error(val rawResponse: WrappedResponse<UserDTO>?) : UpdateAddressState()
    }
}

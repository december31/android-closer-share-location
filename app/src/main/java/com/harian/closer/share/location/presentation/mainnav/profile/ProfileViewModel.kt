package com.harian.closer.share.location.presentation.mainnav.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.country.remote.dto.CountryResponse
import com.harian.closer.share.location.data.user.remote.dto.UserResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.country.entity.CountryEntity
import com.harian.closer.share.location.domain.country.usecase.GetCountryUseCase
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.domain.user.usecase.GetUserInformationUseCase
import com.harian.closer.share.location.platform.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserInformationUseCase: GetUserInformationUseCase,
    private val getCountryUseCase: GetCountryUseCase,
    val sharedPrefs: SharedPrefs
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Init)
    val state: StateFlow<ProfileState> = _state
    fun getUserInformation() {
        viewModelScope.launch {
            getUserInformationUseCase.execute()
                .onStart {

                }
                .catch {
                    it.printStackTrace()
                }
                .collect { baseResult ->
                    when (baseResult) {
                        is BaseResult.Success -> _state.value = ProfileState.SuccessGetUserInformation(baseResult.data)
                        is BaseResult.Error -> _state.value = ProfileState.ErrorGetUserInformation(baseResult.rawResponse)
                    }
                }
        }
    }

    fun getCountry(countryCode: String) {
        viewModelScope.launch {
            getCountryUseCase.execute(countryCode)
                .catch {
                    it.printStackTrace()
                }
                .collect { baseResult ->
                    when (baseResult) {
                        is BaseResult.Error -> _state.value = ProfileState.ErrorGetCountry(baseResult.rawResponse)
                        is BaseResult.Success -> _state.value = ProfileState.SuccessGetCountry(baseResult.data)
                    }
                }
        }
    }

    sealed class ProfileState {
        data object Init : ProfileState()
        data class SuccessGetUserInformation(val user: UserEntity) : ProfileState()
        data class ErrorGetUserInformation(val rawResponse: WrappedResponse<UserResponse>) : ProfileState()
        data class SuccessGetCountry(val country: CountryEntity) : ProfileState()
        data class ErrorGetCountry(val rawResponse: WrappedResponse<CountryResponse>) : ProfileState()
    }
}

package com.harian.closer.share.location.presentation.mainnav.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.data.location.dto.Location
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.location.usecase.DisposeObserverUseCase
import com.harian.closer.share.location.domain.location.usecase.SubscribeFriendsLocationUpdatesUseCase
import com.harian.closer.share.location.domain.location.usecase.UpdateLocationUseCase
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.domain.user.usecase.GetUserInformationUseCase
import com.harian.closer.share.location.utils.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val subscribeFriendsLocationUpdatesUseCase: SubscribeFriendsLocationUpdatesUseCase,
    private val updateLocationUseCase: UpdateLocationUseCase,
    private val disposeObserverUseCase: DisposeObserverUseCase,
    private val getUserInformationUseCase: GetUserInformationUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<NetworkState>(NetworkState.Init)
    val state: StateFlow<NetworkState> get() = _state

    fun subscribeForFriendsLocationUpdates() {
        viewModelScope.launch(Dispatchers.IO) {
            getUserInformationUseCase.execute()
                .catch {
                    it.printStackTrace()
                }
                .collect {
                    (it as? BaseResult.Success)?.data?.let { userEntity ->
                        subscribeFriendsLocationUpdatesUseCase.execute(userEntity)
                            .onStart {
                                this@MapsViewModel.log("start")
                            }
                            .catch { exception ->
                                exception.printStackTrace()
                            }
                            .collect { friend ->
                                if (friend != null) {
                                    _state.emit(NetworkState.GotLocationUpdate(friend))
                                }
                            }
                    }
                }
        }
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            updateLocationUseCase.execute(Location(latitude, longitude))
                .catch { exception ->
                    exception.printStackTrace()
                }
                .collect {

                }
        }
    }

    fun disposeObserver() {
        disposeObserverUseCase.execute()
    }

    sealed class NetworkState {
        data object Init : NetworkState()
        data class GotLocationUpdate(val user: UserEntity) : NetworkState()
    }
}

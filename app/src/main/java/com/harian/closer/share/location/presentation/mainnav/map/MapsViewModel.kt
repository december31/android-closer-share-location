package com.harian.closer.share.location.presentation.mainnav.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.data.location.dto.Location
import com.harian.closer.share.location.domain.location.usecase.DisposeObserverUseCase
import com.harian.closer.share.location.domain.location.usecase.SubscribeFriendsLocationUpdatesUseCase
import com.harian.closer.share.location.domain.location.usecase.UpdateLocationUseCase
import com.harian.closer.share.location.utils.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val subscribeFriendsLocationUpdatesUseCase: SubscribeFriendsLocationUpdatesUseCase,
    private val updateLocationUseCase: UpdateLocationUseCase,
    private val disposeObserverUseCase: DisposeObserverUseCase
) : ViewModel() {
    private val _state = MutableStateFlow("")
    val state: StateFlow<String> get() = _state

    fun subscribeForFriendsLocationUpdates() {
        viewModelScope.launch(Dispatchers.IO) {
            subscribeFriendsLocationUpdatesUseCase.execute()
                .onStart {
                    this@MapsViewModel.log("start")
                }
                .catch {
                    it.printStackTrace()
                }
                .collect {
                    // todo process location data
                    this@MapsViewModel.log(it.toString())
                    _state.emit(it)
                }
        }
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            updateLocationUseCase.execute(Location(latitude, longitude))
                .catch {
                    it.printStackTrace()
                }
                .collect {
                    this@MapsViewModel.log(it)
                }
        }
    }

    fun disposeObserver() {
        disposeObserverUseCase.execute()
    }
}

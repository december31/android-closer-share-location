package com.harian.closer.share.location.presentation.mainnav.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.domain.location.usecase.SubscribeFriendsLocationUpdatesUseCase
import com.harian.closer.share.location.domain.location.usecase.UpdateLocationUseCase
import com.harian.closer.share.location.utils.extension.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val subscribeFriendsLocationUpdatesUseCase: SubscribeFriendsLocationUpdatesUseCase,
    private val updateLocationUseCase: UpdateLocationUseCase
) : ViewModel() {
    fun listenForFriendsLocationChanges() {
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
                }
        }
    }

    fun updateLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            updateLocationUseCase.execute()
                .catch {
                    it.printStackTrace()
                }
                .collect {
                    this@MapsViewModel.log(it)
                }
        }
    }
}

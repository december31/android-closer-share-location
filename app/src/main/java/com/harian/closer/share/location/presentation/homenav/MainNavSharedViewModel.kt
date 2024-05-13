package com.harian.closer.share.location.presentation.homenav

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainNavSharedViewModel @Inject constructor() : ViewModel() {
    private val _centerActionButtonClickLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val centerActionButtonClickLiveData: LiveData<Boolean> = _centerActionButtonClickLiveData

    fun performCenterActionButtonClick() {
        _centerActionButtonClickLiveData.postValue(true)
    }
}

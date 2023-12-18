package com.harian.closer.share.location.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.post.remote.dto.PostResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.domain.post.usecase.GetPopularPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPopularPostsUseCase: GetPopularPostsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<FunctionState>(FunctionState.Init)
    val state: StateFlow<FunctionState> = _state

    fun fetchPopularPosts() {
        viewModelScope.launch {
            getPopularPostsUseCase.execute()
                .onStart {

                }
                .catch {
                    it.printStackTrace()
                }
                .collect { baseResult ->
                    _state.value = FunctionState.Init
                    when (baseResult) {
                        is BaseResult.Success -> _state.value = FunctionState.SuccessGetPopularPosts(baseResult.data)
                        is BaseResult.Error -> _state.value = FunctionState.ErrorGetPopularPosts(baseResult.rawResponse)
                    }
                    _state.value = FunctionState.Init
                }
        }
    }

    sealed class FunctionState {
        data object Init : FunctionState()
        data class SuccessGetPopularPosts(val posts: List<PostEntity>) : FunctionState()
        data class ErrorGetPopularPosts(val rawResponse: WrappedListResponse<PostResponse>?) : FunctionState()
    }
}

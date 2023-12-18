package com.harian.closer.share.location.presentation.post.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.post.remote.dto.CreatePostRequest
import com.harian.closer.share.location.data.post.remote.dto.PostResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.domain.post.usecase.CreatePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<FunctionState>(FunctionState.Init)
    val state: StateFlow<FunctionState> = _state

    fun createPost(createPostRequest: CreatePostRequest) {
        viewModelScope.launch {
            createPostUseCase.execute(createPostRequest)
                .onStart {
                    _state.value = FunctionState.IsLoading(true)
                }
                .catch {
                    it.printStackTrace()
                    _state.value = FunctionState.IsLoading(false)
                    _state.value = FunctionState.ErrorCreatePost(null)
                }
                .collect { baseResult ->
                    _state.value = FunctionState.IsLoading(false)
                    when (baseResult) {
                        is BaseResult.Success -> _state.value = FunctionState.SuccessCreatePost(baseResult.data)
                        is BaseResult.Error -> _state.value = FunctionState.ErrorCreatePost(baseResult.rawResponse)
                    }
                }
        }
    }

    sealed class FunctionState {
        data object Init : FunctionState()
        data class IsLoading(val isLoading: Boolean) : FunctionState()
        data class SuccessCreatePost(val postEntity: PostEntity) : FunctionState()
        data class ErrorCreatePost(val rawResponse: WrappedResponse<PostResponse>?) : FunctionState()
    }
}

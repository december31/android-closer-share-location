package com.harian.closer.share.location.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.post.remote.dto.PostResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.domain.post.usecase.GetPopularPostsUseCase
import com.harian.closer.share.location.domain.post.usecase.LikePostUseCase
import com.harian.closer.share.location.domain.post.usecase.UnlikePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPopularPostsUseCase: GetPopularPostsUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val unlikePostUseCase: UnlikePostUseCase
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

    fun likePost(post: PostEntity) {
        viewModelScope.launch {
            post.id?.let { postId ->
                likePostUseCase.execute(postId)
                    .catch {
                        _state.value = FunctionState.ErrorLikePost(postId)
                    }
                    .collect { baseResult ->
                        when (baseResult) {
                            is BaseResult.Success -> _state.value = FunctionState.SuccessLikePost(baseResult.data)
                            is BaseResult.Error -> _state.value = FunctionState.ErrorLikePost(postId)
                        }
                    }
            }
        }
    }

    fun unlikePost(post: PostEntity) {
        viewModelScope.launch {
            post.id?.let { postId ->
                unlikePostUseCase.execute(postId)
                    .catch {
                        _state.value = FunctionState.ErrorLikePost(postId)
                    }
                    .collect { baseResult ->
                        when (baseResult) {
                            is BaseResult.Success -> _state.value = FunctionState.SuccessUnlikePost(baseResult.data)
                            is BaseResult.Error -> _state.value = FunctionState.ErrorUnlikePost(postId)
                        }
                    }
            }
        }
    }

    sealed class FunctionState {
        data object Init : FunctionState()
        data class SuccessGetPopularPosts(val posts: List<PostEntity>) : FunctionState()
        data class ErrorGetPopularPosts(val rawResponse: WrappedListResponse<PostResponse>?) : FunctionState()
        data class SuccessLikePost(val post: PostEntity) : FunctionState()
        data class ErrorLikePost(val postId: Int) : FunctionState()
        data class SuccessUnlikePost(val post: PostEntity) : FunctionState()
        data class ErrorUnlikePost(val postId: Int) : FunctionState()
    }
}

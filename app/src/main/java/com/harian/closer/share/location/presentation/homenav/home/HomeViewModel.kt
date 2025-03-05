package com.harian.closer.share.location.presentation.homenav.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.post.remote.dto.PostDTO
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
    private val unlikePostUseCase: UnlikePostUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow<ApiState>(ApiState.Init)
    val state: StateFlow<ApiState> = _state

    fun fetchPopularPosts() {
        viewModelScope.launch {
            getPopularPostsUseCase.execute()
                .onStart {

                }
                .catch {
                    it.printStackTrace()
                }
                .collect { baseResult ->
                    _state.value = ApiState.Init
                    when (baseResult) {
                        is BaseResult.Success -> _state.value = ApiState.SuccessGetPopularPosts(baseResult.data)
                        is BaseResult.Error -> _state.value = ApiState.ErrorGetPopularPosts(baseResult.rawResponse)
                    }
                    _state.value = ApiState.Init
                }
        }
    }

    fun likePost(post: PostEntity) {
        viewModelScope.launch {
            likePostUseCase.execute(post)
                .catch {
                    _state.value = ApiState.ErrorLikePost(post)
                }
                .collect { baseResult ->
                    when (baseResult) {
                        is BaseResult.Success -> {
                            _state.value = ApiState.SuccessLikePost(baseResult.data)
                            fetchPopularPosts()
                        }

                        is BaseResult.Error -> _state.value = ApiState.ErrorLikePost(post)
                    }
                }
        }
    }

    fun unlikePost(post: PostEntity) {
        viewModelScope.launch {
            unlikePostUseCase.execute(post)
                .catch {
                    _state.value = ApiState.ErrorLikePost(post)
                }
                .collect { baseResult ->
                    when (baseResult) {
                        is BaseResult.Success -> {
                            _state.value = ApiState.SuccessUnlikePost(baseResult.data)
                            fetchPopularPosts()
                        }

                        is BaseResult.Error -> _state.value = ApiState.ErrorUnlikePost(post)
                    }
                }
        }
    }

    sealed class ApiState {
        data object Init : ApiState()
        data class SuccessGetPopularPosts(val posts: List<PostEntity>) : ApiState()
        data class ErrorGetPopularPosts(val rawResponse: WrappedListResponse<PostDTO>?) : ApiState()
        data class SuccessLikePost(val post: PostEntity) : ApiState()
        data class ErrorLikePost(val post: PostEntity) : ApiState()
        data class SuccessUnlikePost(val post: PostEntity) : ApiState()
        data class ErrorUnlikePost(val post: PostEntity) : ApiState()
    }
}

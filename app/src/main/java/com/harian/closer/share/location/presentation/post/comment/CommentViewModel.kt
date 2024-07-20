package com.harian.closer.share.location.presentation.post.comment

import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.platform.BaseViewModel
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.post.remote.dto.CommentRequest
import com.harian.closer.share.location.data.post.remote.dto.CommentResponse
import com.harian.closer.share.location.data.post.remote.dto.PostDTO
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import com.harian.closer.share.location.domain.comment.entity.CommentEntity
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.domain.post.usecase.CreateCommentUseCase
import com.harian.closer.share.location.domain.post.usecase.GetPostByIdUseCase
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.domain.user.usecase.GetUserInformationUseCase
import com.harian.closer.share.location.utils.runOnMainThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val createCommentUseCase: CreateCommentUseCase,
    private val getUserInformationUseCase: GetUserInformationUseCase,
    private val getPostByIdUseCase: GetPostByIdUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow<State>(State.Init)
    val state: StateFlow<State> = _state.asStateFlow()

    private var post: PostEntity? = null

    fun fetchPostComments(postId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            getPostByIdUseCase.execute(postId)
                .onStart {

                }
                .catch {
                    it.printStackTrace()
                    runOnMainThread {
                        _state.value = State.ErrorGetComments(null)
                    }

                }
                .collect { baseResult ->
                    runOnMainThread {
                        when (baseResult) {
                            is BaseResult.Success -> {
                                this@CommentViewModel.post = baseResult.data
                                _state.value = State.SuccessGetComments(
                                    baseResult.data.comments?.filterNotNull()?.sortedBy { it.createdTime }?.reversed() ?: listOf()
                                )
                            }

                            is BaseResult.Error -> _state.value = State.ErrorGetComments(baseResult.rawResponse)
                        }
                    }
                }
        }
    }

    fun fetchUserData() {
        viewModelScope.launch {
            getUserInformationUseCase.execute()
                .onStart {

                }
                .catch {
                    it.printStackTrace()
                    _state.value = State.ErrorGetUserUserInfo(null)
                }
                .collect { baseResult ->
                    when (baseResult) {
                        is BaseResult.Success -> _state.value = State.SuccessGetUserUserInfo(baseResult.data)
                        is BaseResult.Error -> _state.value = State.ErrorGetUserUserInfo(baseResult.rawResponse)
                    }
                }
        }
    }

    fun createComment(commentRequest: CommentRequest) {
        viewModelScope.launch {
            createCommentUseCase.execute(commentRequest, post)
                .onStart {
                    _state.value = State.IsLoading(true)
                }
                .catch {
                    it.printStackTrace()
                    _state.value = State.IsLoading(false)
                    _state.value = State.ErrorCreateComment(null)
                }
                .collect { baseResult ->
                    _state.value = State.IsLoading(false)
                    when (baseResult) {
                        is BaseResult.Success -> {
                            _state.value = State.SuccessCreateComment(baseResult.data)
                            post?.id?.let { fetchPostComments(it) }
                        }

                        is BaseResult.Error -> _state.value = State.ErrorCreateComment(baseResult.rawResponse)
                    }
                }
        }
    }

    sealed class State {
        data object Init : State()
        data class IsLoading(val isLoading: Boolean) : State()
        data class SuccessGetComments(val comments: List<CommentEntity>) : State()
        data class ErrorGetComments(val rawResponse: WrappedResponse<PostDTO>?) : State()
        data class SuccessGetUserUserInfo(val user: UserEntity) : State()
        data class ErrorGetUserUserInfo(val rawResponse: WrappedResponse<UserDTO>?) : State()
        data class SuccessCreateComment(val commentEntity: CommentEntity) : State()
        data class ErrorCreateComment(val rawResponse: WrappedResponse<CommentResponse>?) : State()
    }
}

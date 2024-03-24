package com.harian.closer.share.location.presentation.post.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.harian.closer.share.location.domain.post.usecase.WatchPostUseCase
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
class PostDetailsViewModel @Inject constructor(
    private val getPostByIdUseCase: GetPostByIdUseCase,
    private val getUserInformationUseCase: GetUserInformationUseCase,
    private val createCommentUseCase: CreateCommentUseCase,
    private val watchPostUseCase: WatchPostUseCase,
    val sharedPrefs: SharedPrefs
) : ViewModel() {
    private val _state = MutableStateFlow<FunctionState>(FunctionState.Init)
    var state: StateFlow<FunctionState> = _state

    var post: PostEntity? = null

    fun fetchPostData(postId: Int) {
        viewModelScope.launch {
            getPostByIdUseCase.execute(postId)
                .onStart {

                }
                .catch {
                    it.printStackTrace()
                    _state.value = FunctionState.ErrorGetPost(null)
                }
                .collect { baseResult ->
                    when (baseResult) {
                        is BaseResult.Success -> {
                            watchPostUseCase.execute(baseResult.data)
                            val transformedPost = transformDataPost(baseResult.data)
                            _state.value = FunctionState.SuccessGetPost(transformedPost)
                        }

                        is BaseResult.Error -> _state.value = FunctionState.ErrorGetPost(baseResult.rawResponse)
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
                    _state.value = FunctionState.ErrorGetUserUserInfo(null)
                }
                .collect { baseResult ->
                    when (baseResult) {
                        is BaseResult.Success -> _state.value = FunctionState.SuccessGetUserUserInfo(baseResult.data)
                        is BaseResult.Error -> _state.value = FunctionState.ErrorGetUserUserInfo(baseResult.rawResponse)
                    }
                }
        }
    }

    fun createComment(commentRequest: CommentRequest) {
        viewModelScope.launch {
            createCommentUseCase.execute(commentRequest, post)
                .onStart {
                    _state.value = FunctionState.IsLoading(true)
                }
                .catch {
                    it.printStackTrace()
                    _state.value = FunctionState.IsLoading(false)
                    _state.value = FunctionState.ErrorCreateComment(null)
                }
                .collect { baseResult ->
                    _state.value = FunctionState.IsLoading(false)
                    when (baseResult) {
                        is BaseResult.Success -> _state.value = FunctionState.SuccessCreateComment(baseResult.data)
                        is BaseResult.Error -> _state.value = FunctionState.ErrorCreateComment(baseResult.rawResponse)
                    }
                }
        }
    }

    private fun transformDataPost(post: PostEntity): ArrayList<Any> {
        this.post = post
        val result = arrayListOf<Any>()
        result.add(post)
        post.images?.let { result.addAll(it) }
        return result
    }

    sealed class FunctionState {
        data object Init : FunctionState()
        data class IsLoading(val isLoading: Boolean) : FunctionState()
        data class SuccessGetPost(val postDataList: List<Any>) : FunctionState()
        data class ErrorGetPost(val rawResponse: WrappedResponse<PostDTO>?) : FunctionState()
        data class SuccessGetUserUserInfo(val userEntity: UserEntity) : FunctionState()
        data class ErrorGetUserUserInfo(val rawResponse: WrappedResponse<UserDTO>?) : FunctionState()
        data class SuccessCreateComment(val commentEntity: CommentEntity) : FunctionState()
        data class ErrorCreateComment(val rawResponse: WrappedResponse<CommentResponse>?) : FunctionState()
    }
}

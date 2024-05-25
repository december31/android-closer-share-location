package com.harian.closer.share.location.presentation.post.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.post.remote.dto.PostDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.domain.post.usecase.CreateCommentUseCase
import com.harian.closer.share.location.domain.post.usecase.GetPostByIdUseCase
import com.harian.closer.share.location.domain.post.usecase.WatchPostUseCase
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
    }
}

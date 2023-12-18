package com.harian.closer.share.location.presentation.post.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.post.remote.dto.PostResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.domain.post.usecase.GetPostByIdUseCase
import com.harian.closer.share.location.presentation.home.HomeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val getPostByIdUseCase: GetPostByIdUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<FunctionState>(FunctionState.Init)
    var state: StateFlow<FunctionState> = _state

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
                            val transformedPost = transformDataPost(baseResult.data)
                            _state.value = FunctionState.SuccessGetPost(transformedPost)
                        }

                        is BaseResult.Error -> _state.value = FunctionState.ErrorGetPost(baseResult.rawResponse)
                    }
                }
        }
    }

    private fun transformDataPost(post: PostEntity): ArrayList<Any> {
        val result = arrayListOf<Any>()
        result.add(post)
        post.comments?.forEach { comment ->
            comment?.let { result.add(it) }
        }
        return result
    }

    sealed class FunctionState {
        data object Init : FunctionState()
        data class SuccessGetPost(val postDataList: List<Any>) : FunctionState()
        data class ErrorGetPost(val rawResponse: WrappedResponse<PostResponse>?) : FunctionState()
    }
}

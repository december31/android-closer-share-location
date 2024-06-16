package com.harian.closer.share.location.presentation.post.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.data.common.BaseViewModel
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.post.remote.dto.PostDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.domain.post.usecase.GetPostByIdUseCase
import com.harian.closer.share.location.domain.post.usecase.LikePostUseCase
import com.harian.closer.share.location.domain.post.usecase.UnlikePostUseCase
import com.harian.closer.share.location.domain.post.usecase.WatchPostUseCase
import com.harian.closer.share.location.platform.SharedPrefs
import com.harian.closer.share.location.utils.runOnMainThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    private val likePostUseCase: LikePostUseCase,
    private val unlikePostUseCase: UnlikePostUseCase,
    val sharedPrefs: SharedPrefs
) : BaseViewModel() {
    private val _state = MutableStateFlow<FetchPostState>(FetchPostState.Init)
    var state: StateFlow<FetchPostState> = _state

    var postLiveData = MutableLiveData<PostEntity>()

    fun fetchPostData(postId: Int) {
        viewModelScope.launch {
            getPostByIdUseCase.execute(postId)
                .onStart {

                }
                .catch {
                    it.printStackTrace()
                    _state.value = FetchPostState.ErrorGetPost(null)
                }
                .collect { baseResult ->
                    when (baseResult) {
                        is BaseResult.Success -> {
                            watchPostUseCase.execute(baseResult.data)
                            val transformedPost = transformDataPost(baseResult.data)
                            _state.value = FetchPostState.SuccessGetPost(transformedPost)
                        }

                        is BaseResult.Error -> _state.value = FetchPostState.ErrorGetPost(baseResult.rawResponse)
                    }
                }
        }
    }

    private fun transformDataPost(post: PostEntity): ArrayList<Any> {
        postLiveData.postValue(post)
        val result = arrayListOf<Any>()
        result.add(post)
        post.images?.let { result.addAll(it) }
        return result
    }

    fun likeOrUnlikePost() {
        if (postLiveData.value?.isLiked == true) {
            likePost()
        } else {
            unlikePost()
        }
    }

    private fun likePost() {
        viewModelScope.launch(Dispatchers.IO) {
            postLiveData.value?.let {
                likePostUseCase.execute(it)
                    .onStart { showLoading() }
                    .catch {
                        it.printStackTrace()
                        hideLoading()
                    }
                    .collect {
                        hideLoading()
                        runOnMainThread {
                            when (it) {
                                is BaseResult.Success -> {
                                    val transformedPost = transformDataPost(it.data)
                                    _state.value = FetchPostState.SuccessGetPost(transformedPost)
                                }

                                is BaseResult.Error -> _state.value = FetchPostState.ErrorGetPost(it.rawResponse)
                            }
                        }
                    }
            }
        }
    }

    private fun unlikePost() {
        viewModelScope.launch {
            postLiveData.value?.let { post ->
                unlikePostUseCase.execute(post)
                    .catch {
                        it.printStackTrace()
                    }
                    .collect { baseResult ->
                        when (baseResult) {
                            is BaseResult.Success -> {
                                val transformedPost = transformDataPost(baseResult.data)
                                _state.value = FetchPostState.SuccessGetPost(transformedPost)
                            }

                            is BaseResult.Error -> _state.value = FetchPostState.ErrorGetPost(baseResult.rawResponse)
                        }
                    }
            }
        }
    }

    sealed class FetchPostState {
        data object Init : FetchPostState()
        data class SuccessGetPost(val postDataList: List<Any>) : FetchPostState()
        data class ErrorGetPost(val rawResponse: WrappedResponse<PostDTO>?) : FetchPostState()
    }

    sealed class LikePostState {
        data object Init : LikePostState()
        data object SuccessLikePost : LikePostState()
        data object ErrorLikePost : LikePostState()
    }

    sealed class UnlikePostState {
        data object Init : UnlikePostState()
        data object SuccessUnlikePost : UnlikePostState()
        data object ErrorUnlikePost : UnlikePostState()
    }
}

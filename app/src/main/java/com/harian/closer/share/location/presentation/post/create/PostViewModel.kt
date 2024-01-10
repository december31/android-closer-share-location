package com.harian.closer.share.location.presentation.post.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.post.remote.dto.PostRequest
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<FunctionState>(FunctionState.Init)
    val state: StateFlow<FunctionState> = _state

    fun createPost(postRequest: PostRequest, images: List<File?>? = null) {
        viewModelScope.launch {
            val parts = buildPostParts(images)
            val body = buildPostBody(postRequest)

            createPostUseCase.execute(parts, body)
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

    /**
     * build body for contents
     */
    private fun buildPostBody(postRequest: PostRequest): RequestBody {
        // build body
        val jsonObject = JSONObject()
        jsonObject.put("title", postRequest.title)
        jsonObject.put("content", postRequest.content)
        return jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    /**
     * build part for images
     */
    private fun buildPostParts(images: List<File?>?): List<MultipartBody.Part>? {
        // build files part
        return images?.map { image ->
            if (image != null) {
                val body = image.asRequestBody()
                return@map MultipartBody.Part.createFormData("image", image.name, body)
            } else {
                return@map null
            }
        }?.filterNotNull()
    }

    sealed class FunctionState {
        data object Init : FunctionState()
        data class IsLoading(val isLoading: Boolean) : FunctionState()
        data class SuccessCreatePost(val postEntity: PostEntity) : FunctionState()
        data class ErrorCreatePost(val rawResponse: WrappedResponse<PostResponse>?) : FunctionState()
    }
}

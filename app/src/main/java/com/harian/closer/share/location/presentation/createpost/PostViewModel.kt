package com.harian.closer.share.location.presentation.createpost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.data.post.remote.dto.CreatePostRequest
import com.harian.closer.share.location.domain.post.usecase.CreatePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase
) : ViewModel() {
    fun createPost(createPostRequest: CreatePostRequest) {
        viewModelScope.launch {
            createPostUseCase.execute(createPostRequest)
                .onStart {

                }
                .catch {
                    it.printStackTrace()
                }
                .collect {

                }
        }
    }
}

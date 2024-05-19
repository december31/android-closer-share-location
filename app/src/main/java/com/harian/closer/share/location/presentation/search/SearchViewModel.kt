package com.harian.closer.share.location.presentation.search

import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.data.common.BaseViewModel
import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.post.remote.dto.PostDTO
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.domain.search.usecase.SearchPostsUseCase
import com.harian.closer.share.location.domain.search.usecase.SearchUsersUseCase
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.utils.runOnMainThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUsersUseCase: SearchUsersUseCase,
    private val searchPostsUseCase: SearchPostsUseCase
) : BaseViewModel() {
    private val _state = MutableStateFlow<SearchState>(SearchState.Init)
    val state: StateFlow<SearchState> get() = _state
    private var searchJob: Job? = null

    fun searchUser(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            if (query.isEmpty()) return@launch
            searchUsersUseCase.execute(query)
                .onStart {
                    runOnMainThread {
                        _state.value = SearchState.Loading(true)
                    }
                }
                .catch {
                    runOnMainThread {
                        _state.value = SearchState.Loading(false)
                    }
                }
                .collect {
                    runOnMainThread {
                        when (it) {
                            is BaseResult.Error -> _state.value = SearchState.SearchUsersFailed(it.rawResponse)
                            is BaseResult.Success -> _state.value = SearchState.SearchUsersSuccessSuccess(it.data)
                        }
                        _state.value = SearchState.Loading(false)
                    }
                }
        }
    }

    fun searchPost(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            if (query.isEmpty()) return@launch
            searchPostsUseCase.execute(query)
                .onStart {
                    runOnMainThread {
                        _state.value = SearchState.Loading(true)
                    }
                }
                .catch {
                    runOnMainThread {
                        _state.value = SearchState.Loading(false)
                    }
                }
                .collect {
                    runOnMainThread {
                        when (it) {
                            is BaseResult.Error -> _state.value = SearchState.SearchPostsFailed(it.rawResponse)
                            is BaseResult.Success -> _state.value = SearchState.SearchPostsSuccessSuccess(it.data)
                        }
                        _state.value = SearchState.Loading(false)
                    }
                }
        }
    }

    sealed class SearchState {
        data object Init : SearchState()
        data class SearchUsersSuccessSuccess(val data: List<UserEntity>) : SearchState()
        data class SearchUsersFailed(val rawResponse: WrappedListResponse<UserDTO>) : SearchState()
        data class SearchPostsSuccessSuccess(val data: List<PostEntity>) : SearchState()
        data class SearchPostsFailed(val rawResponse: WrappedListResponse<PostDTO>) : SearchState()
        data class Loading(val isLoading: Boolean) : SearchState()
    }
}

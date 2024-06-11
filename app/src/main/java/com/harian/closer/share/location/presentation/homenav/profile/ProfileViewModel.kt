package com.harian.closer.share.location.presentation.homenav.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.country.remote.dto.CountryResponse
import com.harian.closer.share.location.data.post.remote.dto.PostDTO
import com.harian.closer.share.location.data.user.remote.dto.FriendsResponse
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.country.entity.CountryEntity
import com.harian.closer.share.location.domain.country.usecase.GetCountryUseCase
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.domain.user.UserRepository
import com.harian.closer.share.location.domain.user.entity.FriendsEntity
import com.harian.closer.share.location.domain.user.entity.ProfileEntity
import com.harian.closer.share.location.domain.user.entity.ProfileType
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.domain.user.usecase.GetFriendsUseCase
import com.harian.closer.share.location.domain.user.usecase.GetPostsUseCase
import com.harian.closer.share.location.domain.user.usecase.GetUserInformationUseCase
import com.harian.closer.share.location.domain.user.usecase.SendFriendRequestUseCase
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
class ProfileViewModel @Inject constructor(
    private val getUserInformationUseCase: GetUserInformationUseCase,
    private val getCountryUseCase: GetCountryUseCase,
    private val getFriendsUseCase: GetFriendsUseCase,
    private val getPostsUseCase: GetPostsUseCase,
    private val sendFriendRequestUseCase: SendFriendRequestUseCase,
    val sharedPrefs: SharedPrefs
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileState>(
        ProfileState.Init(
            listOf(
                ProfileEntity(null, ProfileType.PROFILE),
                ProfileEntity(null, ProfileType.FRIENDS),
                ProfileEntity(null, ProfileType.POSTS),
            )
        )
    )
    val state: StateFlow<ProfileState> = _state

    fun saveState(data: List<ProfileEntity<Any>>) {
        _state.value = ProfileState.Init(data)
    }

    /**
     * fetch user information
     * @param user: the user from which app will request information to server. if user == null app will request information for current logged in user
     */
    fun fetchUserInformation(user: UserEntity? = null) {
        viewModelScope.launch {
            getUserInformationUseCase.execute(user)
                .onStart {

                }
                .catch {
                    it.printStackTrace()
                }
                .collect { baseResult ->
                    when (baseResult) {
                        is BaseResult.Success -> _state.value = ProfileState.SuccessGetUserInformation(baseResult.data)
                        is BaseResult.Error -> _state.value = ProfileState.ErrorGetUserInformation(baseResult.rawResponse)
                    }
                    fetchFriends(user)
                }
        }
    }

    private fun fetchFriends(user: UserEntity?) {
        viewModelScope.launch {
            getFriendsUseCase.execute(user)
                .catch {
                    it.printStackTrace()
                }
                .collect { baseResult ->
                    when (baseResult) {
                        is BaseResult.Error -> _state.value = ProfileState.ErrorGetFriends(baseResult.rawResponse)
                        is BaseResult.Success -> _state.value = ProfileState.SuccessGetFriends(baseResult.data)
                    }
                    fetchPosts(user)
                }
        }
    }

    private fun fetchPosts(user: UserEntity?) {
        viewModelScope.launch {
            getPostsUseCase.execute(user)
                .catch {
                    it.printStackTrace()
                }
                .collect { baseResult ->
                    when (baseResult) {
                        is BaseResult.Error -> _state.value = ProfileState.ErrorGetPosts(baseResult.rawResponse)
                        is BaseResult.Success -> _state.value = ProfileState.SuccessGetPosts(baseResult.data)
                    }
                }
        }
    }

    fun getCountry(countryCode: String) {
        viewModelScope.launch {
            getCountryUseCase.execute(countryCode)
                .catch {
                    it.printStackTrace()
                }
                .collect { baseResult ->
                    when (baseResult) {
                        is BaseResult.Error -> _state.value = ProfileState.ErrorGetCountry(baseResult.rawResponse)
                        is BaseResult.Success -> _state.value = ProfileState.SuccessGetCountry(baseResult.data)
                    }
                }
        }
    }

    fun sendFriendRequest(user: UserEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            sendFriendRequestUseCase.execute(user)
                .catch {
                    it.printStackTrace()
                    runOnMainThread {
                        _state.value = ProfileState.ErrorSendFriendRequest(null)
                    }
                }
                .collect { baseResult ->
                    runOnMainThread {
                        when (baseResult) {
                            is BaseResult.Error -> _state.value = ProfileState.ErrorSendFriendRequest(baseResult.rawResponse)
                            is BaseResult.Success -> _state.value = ProfileState.SuccessSendFriendRequest(baseResult.data)
                        }
                    }
                }
        }
    }

    sealed class ProfileState {
        data class Init(val defaultData: List<ProfileEntity<Any>>) : ProfileState()
        data class SuccessGetUserInformation(val user: UserEntity) : ProfileState()
        data class ErrorGetUserInformation(val rawResponse: WrappedResponse<UserDTO>) : ProfileState()
        data class SuccessGetFriends(val friends: FriendsEntity) : ProfileState()
        data class ErrorGetFriends(val rawResponse: WrappedResponse<FriendsResponse>) : ProfileState()
        data class SuccessGetPosts(val posts: List<PostEntity>) : ProfileState()
        data class ErrorGetPosts(val rawResponse: WrappedListResponse<PostDTO>) : ProfileState()
        data class SuccessGetCountry(val country: CountryEntity) : ProfileState()
        data class ErrorGetCountry(val rawResponse: WrappedResponse<CountryResponse>) : ProfileState()
        data class SuccessSendFriendRequest(val user: UserEntity) : ProfileState()
        data class ErrorSendFriendRequest(val rawResponse: WrappedResponse<UserDTO>?) : ProfileState()
        data object SuccessLogout : ProfileState()
    }
}

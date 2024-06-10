package com.harian.closer.share.location.domain.search.usecase

import com.harian.closer.share.location.data.common.utils.WrappedListResponse
import com.harian.closer.share.location.data.user.remote.dto.UserDTO
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.search.SearchRepository
import com.harian.closer.share.location.domain.user.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchUsersUseCase @Inject constructor(private val searchRepository: SearchRepository){
    suspend fun execute(query: String): Flow<BaseResult<List<UserEntity>, WrappedListResponse<UserDTO>>> {
        return searchRepository.searchUsers(query)
    }
}

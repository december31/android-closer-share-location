package com.harian.closer.share.location.domain.country.usecase

import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.country.remote.dto.CountryResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.country.CountryRepository
import com.harian.closer.share.location.domain.country.entity.CountryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCountryUseCase @Inject constructor(private val countryRepository: CountryRepository) {
    suspend fun execute(countryCode: String): Flow<BaseResult<CountryEntity, WrappedResponse<CountryResponse>>> {
        return countryRepository.getCountry(countryCode)
    }
}

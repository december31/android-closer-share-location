package com.harian.closer.share.location.domain.country

import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.country.remote.dto.CountryResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.country.entity.CountryEntity
import kotlinx.coroutines.flow.Flow

interface CountryRepository {
    suspend fun getCountry(countryCode: String): Flow<BaseResult<CountryEntity, WrappedResponse<CountryResponse>>>
}

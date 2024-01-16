package com.harian.closer.share.location.data.country.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.country.remote.api.CountryApi
import com.harian.closer.share.location.data.country.remote.dto.CountryResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.country.CountryRepository
import com.harian.closer.share.location.domain.country.entity.CountryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CountryRepositoryImpl(private val countryApi: CountryApi) : CountryRepository {
    override suspend fun getCountry(countryCode: String): Flow<BaseResult<CountryEntity, WrappedResponse<CountryResponse>>> {
        return flow {
            val response = countryApi.getCountry(countryCode)
            if (response.isSuccessful && response.code() in 200 until 400) {
                val data = response.body()?.data
                val countryEntity = CountryEntity(
                    code = data?.code,
                    name = data?.name
                )
                emit(BaseResult.Success(countryEntity))
            } else {
                val type = object : TypeToken<WrappedResponse<CountryResponse>>() {}.type
                val err = Gson().fromJson<WrappedResponse<CountryResponse>>(response.errorBody()!!.charStream(), type)!!
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }
}

package com.harian.closer.share.location.data.country.remote.api

import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.country.remote.dto.CountryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CountryApi {
    @GET("country")
    suspend fun getCountry(@Query(value = "country-code") countryCode: String): Response<WrappedResponse<CountryResponse>>
}

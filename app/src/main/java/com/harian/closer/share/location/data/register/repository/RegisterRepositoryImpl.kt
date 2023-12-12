package com.harian.closer.share.location.data.register.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.register.remote.api.RegisterApi
import com.harian.closer.share.location.data.register.remote.dto.RegisterRequest
import com.harian.closer.share.location.data.register.remote.dto.RegisterResponse
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.register.RegisterRepository
import com.harian.closer.share.location.domain.register.entity.RegisterEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(private val registerApi: RegisterApi) :
    RegisterRepository {
    override suspend fun register(registerRequest: RegisterRequest): Flow<BaseResult<RegisterEntity, WrappedResponse<RegisterResponse>>> {
        return flow {
            val response = registerApi.register(registerRequest)
            if (response.isSuccessful){
                val body = response.body()!!
                val registerEntity = RegisterEntity(body.data?.id!!, body.data?.name!!, body.data?.email!!, body.data?.token!!)
                emit(BaseResult.Success(registerEntity))
            }else{
                val type = object : TypeToken<WrappedResponse<RegisterResponse>>(){}.type
                val err : WrappedResponse<RegisterResponse> = Gson().fromJson(response.errorBody()!!.charStream(), type)
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }
}

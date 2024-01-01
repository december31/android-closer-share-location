package com.harian.closer.share.location.data.common.module

import com.harian.closer.share.location.platform.SharedPrefs
import com.harian.closer.share.location.utils.ResponseUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [SharedPrefModule::class])
@InstallIn(SingletonComponent::class)
object ResponseUtilModule {
    @Provides
    @Singleton
    fun provideResponseUtil(sharedPrefs: SharedPrefs): ResponseUtil {
        return ResponseUtil(sharedPrefs)
    }
}

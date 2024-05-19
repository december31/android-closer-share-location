package com.harian.closer.share.location.presentation.di

import com.harian.closer.share.location.platform.SharedPrefs
import com.harian.closer.share.location.presentation.homenav.home.PostAdapter
import com.harian.closer.share.location.presentation.homenav.notification.adapter.FriendRequestAdapter
import com.harian.closer.share.location.presentation.search.UserAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object AdapterModule {

    @Provides
    fun providePostAdapter(sharedPrefs: SharedPrefs): PostAdapter {
        return PostAdapter(sharedPrefs.getToken())
    }

    @Provides
    fun provideFriendRequestAdapter(sharedPrefs: SharedPrefs): FriendRequestAdapter {
        return FriendRequestAdapter(sharedPrefs.getToken())
    }

    @Provides
    fun provideSearchAdapter(sharedPrefs: SharedPrefs): UserAdapter {
        return UserAdapter(sharedPrefs.getToken())
    }
}

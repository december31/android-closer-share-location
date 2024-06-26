package com.harian.closer.share.location.presentation.di

import com.harian.closer.share.location.platform.SharedPrefs
import com.harian.closer.share.location.presentation.homenav.home.PostAdapter
import com.harian.closer.share.location.presentation.homenav.notification.adapter.FriendRequestAdapter
import com.harian.closer.share.location.presentation.post.comment.CommentAdapter
import com.harian.closer.share.location.presentation.search.UserAdapter
import com.harian.closer.share.location.presentation.viewall.friend.FriendAdapter
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

    @Provides
    fun provideCommentAdapter(sharedPrefs: SharedPrefs): CommentAdapter {
        return CommentAdapter(sharedPrefs.getToken())
    }

    @Provides
    fun provideFriendAdapter(sharedPrefs: SharedPrefs): FriendAdapter {
        return FriendAdapter(sharedPrefs.getToken())
    }
}

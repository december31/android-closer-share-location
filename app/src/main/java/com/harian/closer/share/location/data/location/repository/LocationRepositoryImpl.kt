package com.harian.closer.share.location.data.location.repository

import android.annotation.SuppressLint
import android.util.Log
import com.harian.closer.share.location.domain.location.LocationRepository
import com.harian.closer.share.location.platform.SharedPrefs
import com.harian.closer.share.location.utils.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader

class LocationRepositoryImpl(private val stompClient: StompClient, private val sharedPrefs: SharedPrefs) : LocationRepository {

    override suspend fun subscribeFriendsLocationUpdates(): Flow<String> {
        return flow {
            initSessionIfNeed()
            stompClient.topic("/topic/location/subscribe")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ stompMessage ->
                    Log.d(this@LocationRepositoryImpl.javaClass.simpleName, "received: " + stompMessage.payload)
                }, {
                    it.printStackTrace()
                })
        }
    }

    override suspend fun updateLocation(): Flow<String> {
        return flow {
            initSessionIfNeed()
            stompClient.send("/app/location/update", "hello")
                .subscribe({
                    Log.d(this@LocationRepositoryImpl.javaClass.simpleName, "send successful")
                }, {
                    it.printStackTrace()
                })
        }
    }

    @SuppressLint("CheckResult")
    private fun initSessionIfNeed() {
        stompClient.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { lifeCycleEvent ->
                    when (lifeCycleEvent.type) {
                        LifecycleEvent.Type.OPENED -> Log.d(this.javaClass.simpleName, "connection established")
                        LifecycleEvent.Type.CLOSED -> Log.d(this.javaClass.simpleName, "connection closed")
                        LifecycleEvent.Type.ERROR -> lifeCycleEvent.exception.printStackTrace()
                        LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> Log.d(this.javaClass.simpleName, "failed server heartbeat")
                        null -> Unit
                    }
                },
                {
                    it.printStackTrace()
                }
            )
        stompClient.connect(listOf(StompHeader(Constants.AUTHORIZATION, sharedPrefs.getToken())))
    }
}

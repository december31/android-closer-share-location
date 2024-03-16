package com.harian.closer.share.location.data.location.repository

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.Gson
import com.harian.closer.share.location.data.location.dto.Location
import com.harian.closer.share.location.domain.location.LocationRepository
import com.harian.closer.share.location.platform.SharedPrefs
import com.harian.closer.share.location.utils.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader

class LocationRepositoryImpl(private val stompClient: StompClient, private val sharedPrefs: SharedPrefs) : LocationRepository {

    val disposables = CompositeDisposable()

    override suspend fun subscribeFriendsLocationUpdates(): Flow<String> {
        val flow: MutableStateFlow<String> = MutableStateFlow("")
        initSessionIfNeed()
        disposables.add(
            stompClient.topic("/topic/location/subscribe")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({ stompMessage ->
                    flow.value = stompMessage.payload
                }, {
                    it.printStackTrace()
                })
        )
        return flow
    }

    override suspend fun updateLocation(location: Location): Flow<String> {
        return flow {
            initSessionIfNeed()
            stompClient.send("/app/location/update", Gson().toJson(location))
                .subscribe({
                    Log.d(this@LocationRepositoryImpl.javaClass.simpleName, "update location (${location.latitude}, ${location.longitude})")
                }, {
                    it.printStackTrace()
                })
        }
    }

    override fun disposeObserver() {
        disposables.dispose()
        disposables.clear()
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

package com.harian.closer.share.location.data.location.repository

import android.util.Log
import com.google.gson.Gson
import com.harian.closer.share.location.data.location.dto.Location
import com.harian.closer.share.location.domain.location.LocationRepository
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.platform.SharedPrefs
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent

class LocationRepositoryImpl(private val stompClient: StompClient, private val sharedPrefs: SharedPrefs) : LocationRepository {

    private var disposables = CompositeDisposable()

    override suspend fun subscribeFriendsLocationUpdates(userEntity: UserEntity): Flow<UserEntity?> {
        val flow: MutableStateFlow<UserEntity?> = MutableStateFlow(null)
        initSessionIfNeed()
        disposables.add(
            stompClient.topic("/topic/location/subscribe/${userEntity.id}")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({ stompMessage ->
                    flow.value = Gson().fromJson(stompMessage.payload, UserEntity::class.java)
                }, {
                    it.printStackTrace()
                })
        )
        return flow
    }

    override suspend fun updateLocation(location: Location): Flow<String> {
        return flow {
            initSessionIfNeed()
            disposables.add(
                stompClient.send("/app/location/update", Gson().toJson(location))
                    .subscribe({
                        Log.d(
                            this@LocationRepositoryImpl.javaClass.simpleName,
                            "update location (${location.latitude}, ${location.longitude})"
                        )
                    }, {
                        it.printStackTrace()
                    })
            )
        }
    }

    override fun disposeObserver() {
        disposables.dispose()
        disposables = CompositeDisposable()
    }

    private fun initSessionIfNeed() {
        disposables.add(stompClient.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                { lifeCycleEvent ->
                    when (lifeCycleEvent.type) {
                        LifecycleEvent.Type.OPENED -> Log.d(this.javaClass.simpleName, "connection established")
                        LifecycleEvent.Type.CLOSED -> Log.d(this.javaClass.simpleName, "connection closed")
                        LifecycleEvent.Type.ERROR -> Log.e(this.javaClass.simpleName, "error: ${lifeCycleEvent.exception.message}")
                        LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> Log.e(this.javaClass.simpleName, "failed server heartbeat")
                        null -> Unit
                    }
                },
                {
                    it.printStackTrace()
                }
            )
        )
        stompClient.connect()
    }
}

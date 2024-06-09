package com.harian.closer.share.location.presentation.common

import com.google.android.gms.maps.model.Marker
import com.harian.closer.share.location.domain.user.entity.UserEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarkerManager @Inject constructor() {
    private val markerMap = HashMap<Long, Marker?>()

    /**
     * remove marker from map and save a new one to the markerMap
     */
    fun saveMarker(user: UserEntity, marker: Marker?) {
        user.id ?: return
        markerMap[user.id]?.remove()
        markerMap[user.id] = marker
    }

    /**
     * get marker from map
     */
    fun getMarker(user: UserEntity): Marker? {
        return markerMap[user.id]
    }

    fun clearMarkers() {
        markerMap.clear()
    }
}

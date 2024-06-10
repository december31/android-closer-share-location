package com.harian.closer.share.location.utils

import android.content.Context
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun runOnMainThread(block: () -> Unit) {
    withContext(Dispatchers.Main) {
        block.invoke()
    }
}


fun Location.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}

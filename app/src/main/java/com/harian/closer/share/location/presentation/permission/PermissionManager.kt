package com.harian.closer.share.location.presentation.permission

import android.Manifest
import com.harian.software.closer.share.location.R

/**
 * utility class for requesting permission, you need to override inAppPermissionLauncher and intentLauncher
 */
enum class PermissionManager(val permission: Permission) {
    CAMERA(
        Permission(
            titleRes = R.string.permission_camera_title,
            descriptionRes = R.string.permission_camera_description,
            permissions = listOf(Manifest.permission.CAMERA),
            iconRes = R.drawable.ic_access_camera,
            canRequestInApp = true,
        )
    ),

    LOCATION(
        permission = Permission(
            titleRes = R.string.permission_location_title,
            descriptionRes = R.string.permission_location_description,
            permissions = listOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            iconRes = R.drawable.ic_access_location,
            canRequestInApp = true,
        )
    )
}

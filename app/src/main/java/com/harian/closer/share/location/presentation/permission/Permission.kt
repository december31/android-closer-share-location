package com.harian.closer.share.location.presentation.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity

/**
 * an object store data for permissions popup
 * @property permissions a list of permission to be requested. If the permissions can be requested in app then this value will be a list of Manifest.permission.<'permission'>.
 * On the other hand, the permission cannot be request in app, this property will be the action from which goto desire setting screen for the permission
 * @property canRequestInApp indicate weather the permission can be requested in app or not
 * @property checkPermissionFunction a function for checking the permission is granted or not (this property will only take affect if the permission can't be requested in app)
 * @since 22/12/2023
 * @author Duc An
 */
data class Permission(
    val id: Int = -1,
    val titleRes: Int,
    val descriptionRes: Int,
    val permissions: List<String>,
    val iconRes: Int,
    val canRequestInApp: Boolean = true,
    val checkPermissionFunction: (() -> Boolean)? = null,
) {
    private val grantedPermission = arrayListOf<String>()

    var intentLauncher: ActivityResultLauncher<Intent>? = null
    var inAppPermissionsLauncher: ActivityResultLauncher<Array<String>>? = null

    /**
     * invoke when Permission Rationale Dialog shown up but user decline to grant the permission
     */
    var onDecline: (() -> Unit)? = null

    fun isPermissionGranted(context: Context?): Boolean {
        return if (canRequestInApp) {
            grantedPermission.clear()
            grantedPermission.addAll(permissions.filter { permission ->
                context?.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
            })
            grantedPermission.size == permissions.size
        } else {
            checkPermissionFunction?.invoke() == true
        }
    }

    @Suppress("unused")
    fun requestPermission(activity: FragmentActivity?) {
        activity?.let {
            if (canRequestInApp) {
                requestInAppPermission(activity)
            } else {
                showRequestPermissionRationale(activity)
            }
        }
    }

    @Suppress("unused")
    fun requestPermission(activity: AppCompatActivity?) {
        activity?.let {
            if (canRequestInApp) {
                requestInAppPermission(activity)
            } else {
                showRequestPermissionRationale(activity)
            }
        }
    }

    /**
     * request not granted in app permission
     */
    private fun requestInAppPermission(activity: FragmentActivity) {
        permissions.filter { permission ->
            !grantedPermission.contains(permission)
        }.forEach { permission ->
            if (activity.shouldShowRequestPermissionRationale(permission)) {
                showRequestPermissionRationale(activity)
                return
            }
        }
        inAppPermissionsLauncher?.launch(permissions.toTypedArray())
    }

    private fun showRequestPermissionRationale(activity: FragmentActivity) {
        PermissionDialog().apply {
            setData(this@Permission)
            setListener(object : PermissionDialog.Listener {
                override fun onAccept() {
                    gotoSetting(activity)
                }

                override fun onDecline() {
                    onDecline?.invoke()
                }
            })
        }.show(activity.supportFragmentManager, PermissionDialog::class.java.simpleName)
    }

    private fun gotoSetting(activity: Activity) {
        try {
            var intent = Intent(permissions.firstOrNull())
            if (intent.resolveActivity(activity.packageManager) != null) {
                intent.data = Uri.parse("package:${activity.packageName}")
                intentLauncher?.launch(intent)
            } else {
                intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.data = Uri.parse("package:${activity.packageName}")
                intentLauncher?.launch(intent)
            }
        } catch (e: Exception) {
            intentLauncher?.launch(Intent(Settings.ACTION_SETTINGS))
            e.printStackTrace()
        }
    }
}

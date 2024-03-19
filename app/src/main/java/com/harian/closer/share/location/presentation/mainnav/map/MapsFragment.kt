package com.harian.closer.share.location.presentation.mainnav.map

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Looper
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.presentation.common.MarkerManager
import com.harian.closer.share.location.utils.Constants
import com.harian.closer.share.location.utils.extension.dp
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentMapsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class MapsFragment : BaseFragment<FragmentMapsBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_maps

    @Inject
    lateinit var markerManager: MarkerManager

    private val viewModel by viewModels<MapsViewModel>()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var locationInterval = 3000L
    private var marker: Marker? = null
    private var lastTimeMoveCamera = 0L
    private val cameraMovingInterval = 15000
    private var firstTimeGetLocation = true
    private var googleMap: GoogleMap? = null

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                initGoogleMap()
            }

            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                initGoogleMap()
            }

            else -> {
                findNavController().popBackStack()
            }
        }
    }

    override fun setupUI() {
        super.setupUI()
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        handleObserver()
    }

    private fun handleObserver() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                MapsViewModel.NetworkState.Init -> Unit
                is MapsViewModel.NetworkState.GotLocationUpdate -> updateFriendsLocation(it.user)
            }
        }.launchIn(lifecycleScope)
    }

    private fun updateFriendsLocation(user: UserEntity) {
        context?.let { ctx ->
            Glide.with(ctx).downloadOnly().load(user.getAuthorizedAvatarUrl(viewModel.sharedPrefs.getToken())).addListener(
                object : RequestListener<File> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<File>, isFirstResource: Boolean): Boolean {
                        Log.d(this@MapsFragment.javaClass.simpleName, "Download avatar failed")
                        return false
                    }

                    override fun onResourceReady(
                        resource: File,
                        model: Any,
                        target: Target<File>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        lifecycleScope.launch {
                            updateFriendMarker(user, resource)
                        }
                        return false
                    }
                }
            ).submit()
        }
        binding.apply {
            name.text = user.name
            latitude.text = user.latitude.toString()
            longitude.text = user.longitude.toString()
        }
    }

    private suspend fun updateFriendMarker(friend: UserEntity, avatar: File) {
        withContext(Dispatchers.IO) {
            friend.latitude ?: return@withContext
            friend.longitude ?: return@withContext

            val bmOptions = BitmapFactory.Options()
            var bitmap = BitmapFactory.decodeFile(avatar.absolutePath, bmOptions)
            bitmap = Bitmap.createScaledBitmap(bitmap, 40.dp, 40.dp, true)

            val marker = MarkerOptions()
                .position(LatLng(friend.latitude!!, friend.longitude!!))
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))

            Log.d(this@MapsFragment.javaClass.simpleName, "updateFriendMarker: ${friend.name}(${friend.latitude}, ${friend.longitude})")
            withContext(Dispatchers.Main) {
                markerManager.saveMarker(friend, googleMap?.addMarker(marker))
            }
        }
    }

    private fun initGoogleMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync { googleMap ->
            this.googleMap = googleMap
            requestLocationUpdates(googleMap)
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates(googleMap: GoogleMap) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, locationInterval)
            .setWaitForAccurateLocation(false)
            .setMinUpdateDistanceMeters(10f)
            .build()
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let { location ->
                    val currentLocation = LatLng(location.latitude, location.longitude)
                    marker?.remove()
                    marker = googleMap.addMarker(MarkerOptions().position(currentLocation))
                    if (System.currentTimeMillis() - lastTimeMoveCamera > cameraMovingInterval) {
                        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                            currentLocation,
                            if (firstTimeGetLocation) Constants.DEFAULT_MAP_ZOOM_LEVEL else googleMap.cameraPosition.zoom
                        )
                        googleMap.animateCamera(cameraUpdate)
                        lastTimeMoveCamera = System.currentTimeMillis()
                    }
                    firstTimeGetLocation = false
                    viewModel.updateLocation(location.latitude, location.longitude)
                }
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    override fun setupListener() {
        super.setupListener()
        viewModel.subscribeForFriendsLocationUpdates()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.disposeObserver()
    }
}

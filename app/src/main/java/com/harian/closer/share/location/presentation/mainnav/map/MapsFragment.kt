package com.harian.closer.share.location.presentation.mainnav.map

import android.Manifest
import android.annotation.SuppressLint
import android.os.Looper
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.utils.Constants
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentMapsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MapsFragment : BaseFragment<FragmentMapsBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_maps

    private val viewModel by viewModels<MapsViewModel>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    private var locationInterval = 3000L
    private var marker: Marker? = null
    private var lastTimeMoveCamera = 0L
    private val cameraMovingInterval = 15000
    private var firstTimeGetLocation = true

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
        binding.apply {
            name.text = user.name
            latitude.text = user.latitude.toString()
            longitude.text = user.longitude.toString()
        }
    }

    private fun initGoogleMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync { googleMap ->
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
                        Log.d("googleMap.cameraPosition.zoom", googleMap.cameraPosition.zoom.toString())
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

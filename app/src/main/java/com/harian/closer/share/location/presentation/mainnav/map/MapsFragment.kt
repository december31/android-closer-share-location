package com.harian.closer.share.location.presentation.mainnav.map

import android.annotation.SuppressLint
import android.os.Looper
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentMapsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsFragment : BaseFragment<FragmentMapsBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_maps

    private val viewModel by viewModels<MapsViewModel>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    private var locationInterval = 30000L

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {

    }

    override fun setupUI() {
        super.setupUI()
        initGoogleMap()
    }

    private fun initGoogleMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync { googleMap ->
            val sydney = LatLng(-34.0, 151.0)
            googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
            val cameraPosition = CameraPosition.builder()
                .target(sydney)
                .build()
            val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
            googleMap.animateCamera(cameraUpdate)
            requestLocationUpdates()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, locationInterval)
            .setWaitForAccurateLocation(false)
            .setMinUpdateDistanceMeters(10f)
            .build()
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                Log.d(
                    this@MapsFragment.javaClass.simpleName,
                    "Lat: ${locationResult.locations[0].latitude}, Long: ${locationResult.locations[0].longitude}  "
                )
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    override fun setupListener() {
        super.setupListener()
        viewModel.listenForFriendsLocationChanges()
        binding.apply {
            sampleButton.setOnClickListener {
                viewModel.updateLocation()
            }
        }
    }

    private fun requestLocationPermission() {

    }
}

package com.harian.closer.share.location.presentation.homenav.map

import android.Manifest
import android.animation.LayoutTransition
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Looper
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
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
import com.harian.closer.share.location.presentation.homenav.MainNavSharedViewModel
import com.harian.closer.share.location.utils.Constants
import com.harian.closer.share.location.utils.extension.dp
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentMapsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class MapsFragment : BaseFragment<FragmentMapsBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_maps

    @Inject
    lateinit var markerManager: MarkerManager

    private val viewModel by viewModels<MapsViewModel>()
    private val sharedViewModel by activityViewModels<MainNavSharedViewModel>()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var locationInterval = 3000L
    private var currentUserMarker: Marker? = null
    private var lastTimeMoveCamera = 0L
    private val cameraMovingInterval = 15000
    private var firstTimeGetLocation = true
    private var googleMap: GoogleMap? = null
    private val geocoder: Geocoder? by lazy {
        context?.let { Geocoder(it, Locale.US) }
    }

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
        binding.frRoot.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        binding.tvLocation.isSelected = true
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        handleObserver()
    }

    @SuppressLint("MissingPermission")
    private fun handleObserver() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                MapsViewModel.NetworkState.Init -> Unit
                is MapsViewModel.NetworkState.GotLocationUpdate -> updateFriendsLocation(it.user)
            }
        }.launchIn(lifecycleScope)

        sharedViewModel.centerActionButtonClickLiveData.observe(viewLifecycleOwner) {
            if (this::fusedLocationProviderClient.isInitialized) {
                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                        currentUserMarker?.position ?: LatLng(
                            location.latitude,
                            location.longitude
                        ),
                        googleMap?.cameraPosition?.zoom ?: Constants.DEFAULT_MAP_ZOOM_LEVEL
                    )
                    googleMap?.animateCamera(cameraUpdate, 2000, null)
                }
            }
        }
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
    }

    private suspend fun updateFriendMarker(friend: UserEntity, avatar: File) {
        withContext(Dispatchers.IO) {
            friend.latitude ?: return@withContext
            friend.longitude ?: return@withContext

            val marker = markerManager.getMarker(friend)
            if (marker == null) {
                val bmOptions = BitmapFactory.Options()
                var bitmap = BitmapFactory.decodeFile(avatar.absolutePath, bmOptions)
                bitmap = Bitmap.createScaledBitmap(bitmap, 40.dp, 40.dp, true)
                withContext(Dispatchers.Main) {
                    markerManager.saveMarker(
                        friend, googleMap?.addMarker(
                            MarkerOptions()
                                .position(LatLng(friend.latitude!!, friend.longitude!!))
                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        )
                    )
                }
            } else {
                moveMarker(marker, LatLng(friend.latitude!!, friend.longitude!!))
            }

            Log.d(this@MapsFragment.javaClass.simpleName, "updateFriendMarker: ${friend.name}(${friend.latitude}, ${friend.longitude})")
        }
    }

    private fun moveMarker(marker: Marker, toLatLng: LatLng) {
        val typeEvaluator = TypeEvaluator<LatLng> { fraction, startValue, endValue ->
            startValue?.let { startLatLng ->
                endValue?.let { endLatLng ->
                    LatLng(
                        startLatLng.latitude + (endLatLng.latitude - startLatLng.latitude) * fraction,
                        startLatLng.longitude + (endLatLng.longitude - startLatLng.longitude) * fraction
                    )
                }
            } ?: LatLng(0.0, 0.0)
        }
        val animator = ValueAnimator.ofObject(typeEvaluator, marker.position, toLatLng)
        animator?.interpolator = LinearInterpolator()
        animator?.addUpdateListener {
            val latLng = it.animatedValue as LatLng
            marker.position = latLng
        }
        animator?.start()
    }

    private fun initGoogleMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync { googleMap ->
            this.googleMap = googleMap
            requestLocationUpdates(googleMap)
            listenCameraIdle()
        }
    }

    private fun listenCameraIdle() {
        googleMap?.setOnCameraIdleListener {
            lifecycleScope.launch {
                googleMap?.cameraPosition?.target?.let { latLng ->
                    getLocationAddress(latLng)
                        .catch {
                            Log.e(this.javaClass.simpleName, it.message.toString())
                        }
                        .collect {
                            binding.tvLocation.text = it
                        }
                }
            }
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
                    if (currentUserMarker == null) {
                        currentUserMarker = googleMap.addMarker(MarkerOptions().position(currentLocation))
                    } else {
                        moveMarker(currentUserMarker!!, currentLocation)
                    }
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

    private suspend fun getLocationAddress(latLng: LatLng): Flow<String> {
        val locationDeferred = CompletableDeferred<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder?.getFromLocation(latLng.latitude, latLng.longitude, 1) { addresses ->
                locationDeferred.complete(
                    addresses.firstOrNull()?.let {
                        return@let buildAddressLine(it)
                    } ?: getString(R.string.unknown)
                )
            }
        } else {
            @Suppress("DEPRECATION")
            val addresses = geocoder?.getFromLocation(latLng.latitude, latLng.longitude, 1)
            locationDeferred.complete(
                addresses?.firstOrNull()?.let {
                    return@let buildAddressLine(it)
                } ?: getString(R.string.unknown)
            )
        }
        return flow {
            emit(locationDeferred.await())
        }
    }

    private fun buildAddressLine(address: Address): String {
        val sb = StringBuilder()
        if (address.subAdminArea != null) {
            sb.append(address.subAdminArea + ", ")
        }
        if (address.adminArea != null) {
            sb.append(address.adminArea + ", ")
        }
        if (address.countryName != null) {
            sb.append(address.countryName)
        }
        return sb.toString()
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

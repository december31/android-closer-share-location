package com.harian.closer.share.location.presentation.homenav.map

import android.Manifest
import android.animation.LayoutTransition
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.animation.LinearInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
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
import com.harian.closer.share.location.platform.SharedPrefs
import com.harian.closer.share.location.presentation.common.MarkerManager
import com.harian.closer.share.location.presentation.homenav.HomeNavSharedViewModel
import com.harian.closer.share.location.utils.Constants
import com.harian.closer.share.location.utils.extension.toBitmap
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentMapsBinding
import com.harian.software.closer.share.location.databinding.LayoutMapMarkerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
import kotlin.math.abs


@AndroidEntryPoint
class MapsFragment : BaseFragment<FragmentMapsBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_maps

    @Inject
    lateinit var markerManager: MarkerManager

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private val viewModel by viewModels<MapsViewModel>()
    private val sharedViewModel by activityViewModels<HomeNavSharedViewModel>()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var locationInterval = 1000L
    private var currentUserMarker: Marker? = null
    private var firstTimeGetLocation = true
    private var googleMap: GoogleMap? = null
    private var sensorManager: SensorManager? = null
    private var magnetometer: Sensor? = null
    private var accelerometer: Sensor? = null
    private val lastAccelerometer = FloatArray(3)
    private val lastMagnetometer = FloatArray(3)
    private var lastAccelerometerSet = false
    private var lastMagnetometerSet = false
    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)
    private var originalBitmap: Bitmap? = null
    private var calculatingMarker = false
    private var animationDuration = 200L
    private var sensorEventListener: SensorEventListener? = null
    private var lastRotation = 0f
    private var sensorInterval = 500
    private var lastTimeUpdateSensor = System.currentTimeMillis()
    var isRotating: Boolean = false
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSensors()
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
                is MapsViewModel.NetworkState.Init -> Unit
                is MapsViewModel.NetworkState.GotLocationUpdate -> updateFriendsLocation(it.user)
                else -> {}
            }
        }.launchIn(lifecycleScope)

        viewModel.getFriendsState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is MapsViewModel.NetworkState.Init -> Unit
                is MapsViewModel.NetworkState.GotFriends -> {
                    it.friends.forEach { friend ->
                        updateFriendsLocation(friend)
                    }
                }

                else -> Unit
            }
        }.launchIn(lifecycleScope)

        sharedViewModel.centerActionButtonClickLiveData.observe(viewLifecycleOwner) {
            if (it) {
                if (this::fusedLocationProviderClient.isInitialized) {
                    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                        location ?: return@addOnSuccessListener
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
                sharedViewModel.resetCenterActionButtonClick()
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
                withContext(Dispatchers.Main) {
                    var bitmap = avatar.toBitmap(40, 40)

                    val binding = LayoutMapMarkerBinding.inflate(LayoutInflater.from(context))
                    binding.imgAvatar.setImageBitmap(bitmap)
                    binding.tvName.text = friend.name

                    bitmap = binding.lnContainer.toBitmap()
                    markerManager.saveMarker(
                        friend, googleMap?.addMarker(
                            MarkerOptions()
                                .anchor(0.5f, 1f)
                                .position(LatLng(friend.latitude!!, friend.longitude!!))
                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        )
                    )
                }

            } else {
                withContext(Dispatchers.Main) {
                    moveMarker(marker, LatLng(friend.latitude!!, friend.longitude!!))
                }
            }

            Log.d(this@MapsFragment.javaClass.simpleName, "updateFriendMarker: ${friend.name}(${friend.latitude}, ${friend.longitude})")
        }
    }

    /**
     * these logic is referred from this site
     * @see <a href="https://stackoverflow.com/a/44993432/17950866">Stack overflow</a>
     */
    private fun rotateMarker(toRotation: Float) {
        currentUserMarker ?: return
        if (!isRotating) {
            isRotating = true
            val handler = Handler(Looper.getMainLooper())
            val start = SystemClock.uptimeMillis()
            val startRotation: Float = currentUserMarker!!.rotation
            val duration: Long = 1000
            val deltaRotation = abs(toRotation - startRotation) % 360
            val rotation = (if (deltaRotation > 180) 360 - deltaRotation else deltaRotation) *
                    (if (((toRotation - startRotation) in 0.0..180.0) || (toRotation - startRotation <= -180 && toRotation - startRotation >= -360)) 1 else -1)

            val interpolator = LinearInterpolator()
            handler.post(object : Runnable {
                override fun run() {
                    val elapsed = SystemClock.uptimeMillis() - start
                    val t = interpolator.getInterpolation(elapsed.toFloat() / duration)
                    currentUserMarker!!.rotation = (startRotation + t * rotation) % 360
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16)
                    } else {
                        isRotating = false
                    }
                }
            })
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
            googleMap.mapType = sharedPrefs.getMapType()
            requestLocationUpdates(googleMap)
            listenCameraIdle()
            startSensors()
            viewModel.fetchFriends()
        }
    }

    private fun initSensors() {
        sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        originalBitmap = ContextCompat.getDrawable(requireContext(), R.drawable.ic_current_location)?.toBitmap()
        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event ?: return
                if ((System.currentTimeMillis() - lastTimeUpdateSensor) < sensorInterval) return

                lastTimeUpdateSensor = System.currentTimeMillis()
                if (event.sensor === magnetometer) {
                    System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.size)
                    lastMagnetometerSet = true
                } else if (event.sensor === accelerometer) {
                    System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.size)
                    lastAccelerometerSet = true
                }

                if (lastAccelerometerSet && lastMagnetometerSet && !calculatingMarker) {
                    lifecycleScope.launch {
                        calculatingMarker = true
                        SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer)
                        SensorManager.getOrientation(rotationMatrix, orientation)

                        val azimuthInRadians = orientation[0]
                        val azimuthInDegrees = (Math.toDegrees(azimuthInRadians.toDouble()) + 360).toFloat() % 360

                        if (lastRotation != azimuthInDegrees) {
                            rotateMarker(azimuthInDegrees)
                            delay(animationDuration)
                        }
                        lastRotation = azimuthInRadians
                        calculatingMarker = false
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // do nothing
            }
        }
    }

    private fun startSensors() {
        sensorManager?.registerListener(sensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager?.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    private fun stopSensor() {
        sensorManager?.unregisterListener(sensorEventListener)
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
                        currentUserMarker = googleMap.addMarker(
                            MarkerOptions()
                                .position(currentLocation)
                                .icon(originalBitmap?.let { BitmapDescriptorFactory.fromBitmap(it) })
                                .anchor(0.5f, 0.5f)
                                .rotation(location.bearing)
                        )
                    } else {
                        moveMarker(currentUserMarker!!, currentLocation)
                    }
                    if (firstTimeGetLocation) {
                        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                            currentLocation,
                            Constants.DEFAULT_MAP_ZOOM_LEVEL
                        )
                        googleMap.animateCamera(cameraUpdate)
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

        binding.btnMapType.setOnClickListener {
            MapTypeBottomSheetDialog().apply {
                onSelectMapType = { mapType ->
                    googleMap?.mapType = mapType
                    dismiss()
                }
            }.show(childFragmentManager, MapTypeBottomSheetDialog::class.java.simpleName)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.disposeObserver()
        markerManager.clearMarkers()
        stopSensor()
    }
}

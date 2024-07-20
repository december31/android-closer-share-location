package com.harian.closer.share.location.presentation.setting.address

import android.Manifest
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
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.animation.LinearInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.harian.closer.share.location.platform.BaseViewModel
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.platform.SharedPrefs
import com.harian.closer.share.location.presentation.homenav.map.MapTypeBottomSheetDialog
import com.harian.closer.share.location.presentation.permission.PermissionManager
import com.harian.closer.share.location.utils.Constants
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentUpdateAddressBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class UpdateAddressFragment : BaseFragment<FragmentUpdateAddressBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_update_address

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private val viewModel: UpdateAddressViewModel by viewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var locationInterval = 3000L
    private var marker: Marker? = null
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
    private var lastRotation = 0f
    private var sensorEventListener: SensorEventListener? = null
    private var mapFragment: SupportMapFragment? = null
    private var isRotating: Boolean = false
    private val geocoder: Geocoder? by lazy {
        context?.let { Geocoder(it, Locale.getDefault()) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSensors()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopSensor()
    }

    override fun getFragmentViewModel(): BaseViewModel = viewModel

    override fun setupPermission() {
        super.setupPermission()
        PermissionManager.LOCATION.permission.apply {
            inAppPermissionsLauncher = registerForActivityResult(
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
            intentLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    if (isPermissionGranted(context)) {
                        initGoogleMap()
                    } else {
                        findNavController().popBackStack()
                    }
                }
        }
    }

    private fun initSensors() {
        sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        originalBitmap = ContextCompat.getDrawable(requireContext(), R.drawable.ic_current_location)?.toBitmap()
        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event!!.sensor === magnetometer) {
                    System.arraycopy(event!!.values, 0, lastMagnetometer, 0, event.values.size)
                    lastMagnetometerSet = true
                } else if (event!!.sensor === accelerometer) {
                    System.arraycopy(event!!.values, 0, lastAccelerometer, 0, event.values.size)
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

    override fun setupUI() {
        super.setupUI()
        PermissionManager.LOCATION.permission.requestPermission(activity)
    }

    override fun handleStateChanges() {
        super.handleStateChanges()
        viewModel.updateAddressState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                UpdateAddressViewModel.UpdateAddressState.Init -> Unit
                is UpdateAddressViewModel.UpdateAddressState.Error -> showToast(R.string.some_thing_went_wrong_please_try_again_later)
                is UpdateAddressViewModel.UpdateAddressState.Success -> {
                    showToast(R.string.address_updated)
                    findNavController().popBackStack()
                }
            }
        }.launchIn(lifecycleScope)
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            btnFocusCurrentLocation.setOnClickListener {
                marker?.position?.let { it1 ->
                    val cameraUpdate = CameraUpdateFactory.newCameraPosition(
                        CameraPosition.builder()
                            .target(it1)
                            .zoom(Constants.DEFAULT_MAP_ZOOM_LEVEL)
                            .build()
                    )
                    googleMap?.animateCamera(cameraUpdate, 1000, null)
                }
            }

            btnToggleFullScreen.setOnClickListener {
                lnBottomDetailsLocation.isVisible = binding.lnBottomDetailsLocation.isVisible.not()
                btnToggleFullScreen.setImageResource(
                    if (lnBottomDetailsLocation.isVisible)
                        R.drawable.ic_toggle_full_screen
                    else
                        R.drawable.ic_toggle_exit_full_screen
                )
            }

            btnMapType.setOnClickListener {
                MapTypeBottomSheetDialog().apply {
                    onSelectMapType = { mapType ->
                        googleMap?.mapType = mapType
                        dismiss()
                    }
                }.show(childFragmentManager, MapTypeBottomSheetDialog::class.java.simpleName)
            }
            icBack.setOnClickListener {
                findNavController().popBackStack()
            }
            btnSave.setOnClickListener {
                updateLocationDetails(googleMap?.cameraPosition?.target)
                viewModel.updateAddress(edtAddress.text.toString())
            }
        }
    }

    private fun initGoogleMap() {
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync { googleMap ->
            this.googleMap = googleMap
            googleMap.mapType = sharedPrefs.getMapType()
            requestLocationUpdates(googleMap)
            listenCameraChanges(googleMap)
            startSensors()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates(googleMap: GoogleMap) {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            updateMarker(googleMap, it)
        }
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, locationInterval)
            .setWaitForAccurateLocation(false)
            .setMinUpdateDistanceMeters(10f)
            .build()
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                updateMarker(googleMap, locationResult.lastLocation)
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun listenCameraChanges(googleMap: GoogleMap) {
        googleMap.setOnCameraIdleListener {
            binding.btnSave.isEnabled = true
            binding.btnSave.alpha = 1f
            updateLocationDetails(googleMap.cameraPosition.target)
        }

        googleMap.setOnCameraMoveStartedListener {
            binding.btnSave.alpha = 0.5f
            binding.btnSave.isEnabled = false
        }
    }

    private fun updateLocationDetails(latLng: LatLng?) {
        latLng ?: return
        lifecycleScope.launch {
            binding.apply {
                getLocationAddress(latLng)
                    .catch {
                        edtAddress.setText(R.string.unknown)
                    }
                    .collectLatest {
                        binding.edtAddress.setText(it)
                    }
            }
        }
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

    @Synchronized
    private fun updateMarker(googleMap: GoogleMap, location: Location?) {
        location ?: return
        val currentLocation = LatLng(location.latitude, location.longitude)
        if (marker == null) {
            marker = googleMap.addMarker(
                MarkerOptions()
                    .position(currentLocation)
                    .icon(originalBitmap?.let { BitmapDescriptorFactory.fromBitmap(it) })
                    .anchor(0.5f, 0.5f)
                    .rotation(location.bearing)
            )
        } else {
            moveMarker(currentLocation)
        }
        if (firstTimeGetLocation) {
            firstTimeGetLocation = false
            val cameraUpdate = CameraUpdateFactory.newCameraPosition(
                CameraPosition.builder()
                    .target(currentLocation)
                    .zoom(Constants.DEFAULT_MAP_ZOOM_LEVEL)
                    .build()
            )
            googleMap.animateCamera(cameraUpdate)
        }
    }

    /**
     * these logic is referred from this site
     * @see <a href="https://stackoverflow.com/a/44993432/17950866">Stack overflow</a>
     */
    @Synchronized
    private fun rotateMarker(toRotation: Float) {
        marker ?: return
        if (!isRotating) {
            isRotating = true
            val handler = Handler(Looper.getMainLooper())
            val start = SystemClock.uptimeMillis()
            val startRotation = marker!!.rotation
            val duration: Long = 1000
            val deltaRotation = abs(toRotation - startRotation) % 360
            val rotation = (if (deltaRotation > 180) 360 - deltaRotation else deltaRotation) *
                    (if ((toRotation - startRotation in 0.0..180.0) || (toRotation - startRotation <= -180 && toRotation - startRotation >= -360)) 1 else -1)

            val interpolator = LinearInterpolator()
            handler.post(object : Runnable {
                override fun run() {
                    val elapsed = SystemClock.uptimeMillis() - start
                    val t = interpolator.getInterpolation(elapsed.toFloat() / duration)
                    marker!!.rotation = (startRotation + t * rotation) % 360
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

    private fun moveMarker(toLatLng: LatLng) {
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
        val animator = ValueAnimator.ofObject(typeEvaluator, marker?.position, toLatLng)
        animator?.interpolator = LinearInterpolator()
        animator?.addUpdateListener {
            val latLng = it.animatedValue as LatLng
            marker?.position = latLng
        }
        animator?.start()
    }

}

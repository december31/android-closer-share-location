package com.harian.closer.share.location.presentation.addfriend

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.WindowInsets
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.utils.extension.Animation
import com.harian.closer.share.location.utils.extension.findGlobalNavController
import com.harian.closer.share.location.utils.extension.navigateWithAnimation
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentScanQrCodeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor

@AndroidEntryPoint
class ScanQrCodeFragment : BaseFragment<FragmentScanQrCodeBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_scan_qr_code

    private val viewModel: ScanQrCodeViewModel by viewModels()

    private lateinit var cameraPermissionActivityResult: ActivityResultLauncher<String>
    private lateinit var cameraPermissionFromSettingActivityResult: ActivityResultLauncher<Intent>
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    private var cameraProvider: ProcessCameraProvider? = null
    private var preview: Preview? = null
    private var cameraSelector: CameraSelector? = null

    private var isScanEnabled = true

    @ExperimentalGetImage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerActivityResult()
    }

    @SuppressLint("WrongConstant")
    override fun setupSystemBarBehavior() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val insets = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.statusBars())
                binding.root.setPadding(0, 0, 0, insets.bottom)
            }
            WindowInsetsCompat.CONSUMED
        }

    }

    @ExperimentalGetImage
    override fun setupUI() {
        super.setupUI()
        binding.apply {
            btnMyQrCode.isSelected = true
            btnGallery.isSelected = true
        }
        requestPermission()
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            btnBack.setOnClickListener {
                findGlobalNavController()?.popBackStack()
            }
            btnGallery.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            btnMyQrCode.setOnClickListener {
                findNavController().navigateWithAnimation(
                    ScanQrCodeFragmentDirections.actionScanQrCodeFragmentToMyQrCodeFragment(),
                    Animation.SlideUp
                )
            }
        }
    }

    override fun handleStateChanges() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is ScanQrCodeViewModel.GetUserInformationState.Init -> Unit
                is ScanQrCodeViewModel.GetUserInformationState.Error -> showToast(it.message)
                is ScanQrCodeViewModel.GetUserInformationState.Loading -> binding.frLoading.isVisible = it.isLoading
                is ScanQrCodeViewModel.GetUserInformationState.Success -> {
                    findGlobalNavController()?.navigateWithAnimation(
                        ScanQrCodeFragmentDirections.actionScanQrCodeFragmentToProfileFragment(it.user),
                        Animation.SlideLeft
                    )
                }
            }
        }.launchIn(lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.resetState()
        isScanEnabled = true
    }

    @ExperimentalGetImage
    private fun requestPermission() {
        if (context?.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                cameraPermissionFromSettingActivityResult.launch(makeAppDetailsIntent())
            } else {
                cameraPermissionActivityResult.launch(Manifest.permission.CAMERA)
            }
        } else {
            initiateCamera()
        }
    }

    @ExperimentalGetImage
    private fun registerActivityResult() {
        cameraPermissionActivityResult = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result == true) {
                initiateCamera()
            } else {
                findNavController().popBackStack()
            }
        }

        cameraPermissionFromSettingActivityResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { _ ->
            if (context?.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                initiateCamera()
            } else {
                findNavController().popBackStack()
            }
        }

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri ?: return@registerForActivityResult
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val barcodeOption =
                BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .build()
            val barcodeScanner = BarcodeScanning.getClient(barcodeOption)

            barcodeScanner.process(InputImage.fromBitmap(bitmap, 0)).addOnSuccessListener {
                if (it.isNotEmpty()) {
                    viewModel.processQrCodeResult(it.getOrNull(0)?.displayValue)
                } else {
                    showToast(R.string.did_not_find_any_qr_code)
                }
            }
            inputStream?.close()
        }
    }

    @SuppressLint("RestrictedApi")
    @ExperimentalGetImage
    private fun initiateCamera() {
        val listenableFuture: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(requireActivity().applicationContext)

        listenableFuture.addListener({
            try {
                cameraProvider = listenableFuture.get() as ProcessCameraProvider
                preview = Preview.Builder().build()

                cameraSelector =
                    CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()

                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    this,
                    cameraSelector!!,
                    preview,
                    getQrCodeDetector { qrCode, _ ->
                        isScanEnabled = false
                        viewModel.processQrCodeResult(qrCode.displayValue)
                        cameraProvider?.unbindAll()
                    })

                preview?.setSurfaceProvider(binding.cameraPreview.surfaceProvider)

            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @ExperimentalGetImage
    private fun getQrCodeDetector(onQrCodeDetected: (qrCode: Barcode, image: ImageProxy) -> Unit): ImageAnalysis {
        val barcodeOption =
            BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build()
        val barcodeScanner = BarcodeScanning.getClient(barcodeOption)

        val analyzer = ImageAnalysis.Analyzer { imageProxy ->
            if (imageProxy.image == null) return@Analyzer

            barcodeScanner.process(InputImage.fromMediaImage(imageProxy.image!!, 0))
                .addOnSuccessListener {
                    if (it.isNotEmpty()) {
                        if (isScanEnabled) {
                            onQrCodeDetected.invoke(it[0], imageProxy)
                        }
                    }
                }.addOnCompleteListener {
                    imageProxy.close()
                }
        }

        val imageAnalysis = ImageAnalysis.Builder().build()
        imageAnalysis.setAnalyzer(MyExecutor(), analyzer)

        return imageAnalysis
    }

    inner class MyExecutor : Executor {
        private val handler = android.os.Handler(Looper.getMainLooper())
        override fun execute(runnable: Runnable?) {
            runnable?.let { handler.post(it) }
        }
    }
}

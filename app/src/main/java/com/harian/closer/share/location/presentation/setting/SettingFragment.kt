package com.harian.closer.share.location.presentation.setting

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.harian.closer.share.location.data.common.BaseViewModel
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.platform.SharedPrefs
import com.harian.closer.share.location.presentation.permission.PermissionManager
import com.harian.closer.share.location.presentation.setting.avatar.CropImageDialog
import com.harian.closer.share.location.presentation.setting.avatar.SelectImageBottomSheet
import com.harian.closer.share.location.utils.clearCache
import com.harian.closer.share.location.utils.extension.Animation
import com.harian.closer.share.location.utils.extension.glideLoadImage
import com.harian.closer.share.location.utils.extension.navigateWithAnimation
import com.harian.software.closer.share.location.BuildConfig
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentSettingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_setting

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private val viewModel: SettingViewModel by viewModels()

    private var uri: Uri? = null
    private var selectImageBottomSheet: SelectImageBottomSheet? = null

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        selectImageBottomSheet?.dismiss()
        if (isSuccess && uri != null) {
            makeUICropImage(uri!!)
        } else {
            Toast.makeText(context, getString(R.string.failed_to_take_picture), Toast.LENGTH_SHORT).show()
        }
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        selectImageBottomSheet?.dismiss()
        if (uri != null) {
            makeUICropImage(uri)
        } else {
            Toast.makeText(context, getString(R.string.failed_to_take_picture), Toast.LENGTH_SHORT).show()
        }
    }

    override fun getFragmentViewModel(): BaseViewModel = viewModel

    override fun setupUI() {
        super.setupUI()
        uri = createTempPictureUri()
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            icBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnLogout.setOnClickListener {
                viewModel.logout()
            }

            icQrCode.setOnClickListener {
                findNavController().navigateWithAnimation(SettingFragmentDirections.actionSettingFragmentToMyQrCodeFragment(), Animation.SlideUp)
            }

            viewRowAvatar.setOnClickListener {
                makeUISelectImage()
            }

            viewRowUserName.setOnClickListener {
                findNavController().navigateWithAnimation(
                    SettingFragmentDirections.actionSettingFragmentToUpdateUsernameFragment(),
                    Animation.SlideLeft
                )
            }

            viewRowModifyPassword.setOnClickListener {
                findNavController().navigateWithAnimation(
                    SettingFragmentDirections.actionSettingFragmentToUpdatePasswordFragment(),
                    Animation.SlideLeft
                )
            }

            viewRowAddress.setOnClickListener {
                findNavController().navigateWithAnimation(
                    SettingFragmentDirections.actionSettingFragmentToUpdateAddressFragment(),
                    Animation.SlideLeft
                )
            }
        }
    }

    override fun setupPermission() {
        PermissionManager.CAMERA.permission.apply {
            inAppPermissionsLauncher =
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                    if (isPermissionGranted(context)) {
                        uri ?: return@registerForActivityResult
                        takePictureLauncher.launch(uri)
                    }
                }
            intentLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    if (isPermissionGranted(context)) {
                        uri ?: return@registerForActivityResult
                        takePictureLauncher.launch(uri)
                    }
                }
        }
    }

    override fun handleStateChanges() {
        super.handleStateChanges()
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                SettingViewModel.SettingState.Init -> Unit
                SettingViewModel.SettingState.ErrorLogout -> showToast(R.string.some_thing_went_wrong_please_try_again_later)
                SettingViewModel.SettingState.SuccessLogout -> handleSuccessLogout()
                is SettingViewModel.SettingState.ErrorGetUserInformation -> handleErrorGetUserInformation()
                is SettingViewModel.SettingState.SuccessGetUserInformation -> handleSuccessGetUserInformation(it.user)
            }
        }.launchIn(lifecycleScope)

        viewModel.updateAvatarState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                SettingViewModel.UpdateAvatarState.Init -> Unit
                is SettingViewModel.UpdateAvatarState.FailedUpdateAvatar -> showToast(R.string.some_thing_went_wrong_please_try_again_later)
                is SettingViewModel.UpdateAvatarState.SuccessUpdateAvatar -> handleSuccessUpdateAvatar(it.data)
            }
        }.launchIn(lifecycleScope)

        viewModel.fetchUserInfo()
    }

    private fun handleSuccessUpdateAvatar(user: UserEntity) {
        binding.imgAvatar.glideLoadImage(user.getAuthorizedAvatarUrl(sharedPrefs.getToken()))
    }

    private fun handleSuccessLogout() {
        activity?.apply {
            finish()
            startActivity(intent)
            clearCache()
        }
    }

    private fun handleErrorGetUserInformation() {
        showToast(R.string.some_thing_went_wrong_please_try_again_later)
        findNavController().popBackStack()
    }

    private fun handleSuccessGetUserInformation(user: UserEntity) {
        binding.apply {
            imgAvatar.glideLoadImage(user.getAuthorizedAvatarUrl(sharedPrefs.getToken()))
            viewRowUserName.setTextEnd(user.name)
            tvEmail.text = user.email
            tvPhoneNumber.text = user.phoneNumber
            tvAddress.text = user.address

            btnBindPhoneNumber.isVisible = user.phoneNumber.isNullOrBlank()
            btnBindEmail.isVisible = user.email.isNullOrBlank()
            btnBindAddress.isVisible = user.address.isNullOrBlank()

            tvPhoneNumber.isVisible = !user.phoneNumber.isNullOrBlank()
            tvEmail.isVisible = !user.email.isNullOrBlank()
            tvAddress.isVisible = !user.address.isNullOrBlank()
        }
    }

    private fun createTempPictureUri(
        provider: String = "${BuildConfig.APPLICATION_ID}.provider",
        fileName: String = "picture_${System.currentTimeMillis()}",
        fileExtension: String = ".png"
    ): Uri? {
        val tempFile = File.createTempFile(
            fileName, fileExtension, context?.cacheDir
        ).apply {
            createNewFile()
        }

        return context?.applicationContext?.let { FileProvider.getUriForFile(it, provider, tempFile) }
    }

    private fun createTempPictureFile(bitmap: Bitmap): File {
        val tempFile = File.createTempFile(
            "avatar_${System.currentTimeMillis()}",
            ".jpg",
            context?.cacheDir
        )
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, tempFile.outputStream())
        return tempFile
    }

    private fun makeUISelectImage() {
        selectImageBottomSheet = SelectImageBottomSheet().apply {
            setListener(object : SelectImageBottomSheet.Listener {
                override fun onTakePicture() {
                    PermissionManager.CAMERA.permission.requestPermission(activity)
                }

                override fun onSelectFromAlbum() {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            })
        }
        selectImageBottomSheet?.show(childFragmentManager, SelectImageBottomSheet::class.java.simpleName)
    }

    private fun makeUICropImage(uri: Uri) {
        CropImageDialog().apply {
            setUri(uri)
            setListener(object : CropImageDialog.Listener {
                override fun onSelect(bitmap: Bitmap?) {
                    bitmap ?: return
                    lifecycleScope.launch(Dispatchers.IO) {
                        val file = createTempPictureFile(bitmap)
                        viewModel.updateAvatar(file)
                    }
                }
            })
        }.show(childFragmentManager, null)
    }
}

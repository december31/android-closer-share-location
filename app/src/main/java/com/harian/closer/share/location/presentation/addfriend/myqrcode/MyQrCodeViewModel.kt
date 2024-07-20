package com.harian.closer.share.location.presentation.addfriend.myqrcode

import android.graphics.Bitmap
import android.graphics.Color
import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.harian.closer.share.location.platform.BaseViewModel
import com.harian.closer.share.location.domain.common.base.BaseResult
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.domain.user.usecase.GetUserInformationUseCase
import com.harian.software.closer.share.location.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Hashtable
import javax.inject.Inject

@HiltViewModel
class MyQrCodeViewModel @Inject constructor(
    private val getUserInformationUseCase: GetUserInformationUseCase
) : BaseViewModel() {

    private val _state = MutableStateFlow<GetUserInformationState>(GetUserInformationState.Init)
    val state: StateFlow<GetUserInformationState> = _state

    private val _generateQrCodeState = MutableStateFlow<GenerateQrCodeState>(GenerateQrCodeState.Init)
    val generateQrCodeState: StateFlow<GenerateQrCodeState> = _generateQrCodeState

    fun resetStates() {
        _state.value = GetUserInformationState.Init
        _generateQrCodeState.value = GenerateQrCodeState.Init
    }

    fun fetchUserInformation() {
        viewModelScope.launch {
            getUserInformationUseCase.execute()
                .onStart {
                    withContext(Dispatchers.Main) {
                        _state.value = GetUserInformationState.Loading(true)
                    }
                }
                .catch {
                    withContext(Dispatchers.Main) {
                        _state.value = GetUserInformationState.Loading(false)
                        _state.value = GetUserInformationState.Error(R.string.error_get_user_information)
                    }
                }
                .collect {
                    withContext(Dispatchers.Main) {
                        when (it) {
                            is BaseResult.Error -> {
                                _state.value = GetUserInformationState.Error(R.string.error_get_user_information)
                            }

                            is BaseResult.Success -> {
                                _state.value = GetUserInformationState.Success(it.data)
                                generateQrCode(it.data.id.toString())
                            }
                        }
                        _state.value = GetUserInformationState.Loading(false)
                    }
                }
        }
    }

    private fun generateQrCode(myCodeText: String?) {
        _generateQrCodeState.value = GenerateQrCodeState.Loading(true)
        viewModelScope.launch(Dispatchers.IO) {
            val hintMap = Hashtable<EncodeHintType, Any?>()
            hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H // H = 30% damage
            hintMap[EncodeHintType.MARGIN] = 1
            val qrCodeWriter = QRCodeWriter()
            val size = 512
            val bitMatrix: BitMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, size, size, hintMap)
            val width: Int = bitMatrix.width
            val bmp = Bitmap.createBitmap(width, width, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until width) {
                    bmp.setPixel(y, x, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            withContext(Dispatchers.Main) {
                _generateQrCodeState.value = GenerateQrCodeState.Success(bmp)
                _generateQrCodeState.value = GenerateQrCodeState.Loading(false)
            }
        }
    }

    sealed class GetUserInformationState {
        data object Init : GetUserInformationState()
        data class Loading(val isLoading: Boolean) : GetUserInformationState()
        data class Success(val user: UserEntity) : GetUserInformationState()
        data class Error(@StringRes val message: Int) : GetUserInformationState()
    }

    sealed class GenerateQrCodeState {
        data object Init : GenerateQrCodeState()
        data class Loading(val isLoading: Boolean) : GenerateQrCodeState()
        data class Success(val qrCode: Bitmap) : GenerateQrCodeState()
    }
}

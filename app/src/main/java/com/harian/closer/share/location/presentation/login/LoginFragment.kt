package com.harian.closer.share.location.presentation.login

import android.annotation.SuppressLint
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.harian.closer.share.location.data.common.utils.WrappedResponse
import com.harian.closer.share.location.data.login.remote.dto.LoginRequest
import com.harian.closer.share.location.data.register.remote.dto.RegisterRequest
import com.harian.closer.share.location.data.register.remote.dto.RegisterResponse
import com.harian.closer.share.location.data.request.otp.remote.dto.RequestOtpRequest
import com.harian.closer.share.location.platform.AppManager
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.presentation.login.state.LoginState
import com.harian.closer.share.location.presentation.login.state.RegisterState
import com.harian.closer.share.location.presentation.login.state.ResetPasswordState
import com.harian.closer.share.location.presentation.login.state.State
import com.harian.closer.share.location.presentation.login.state.VerificationState
import com.harian.closer.share.location.utils.extension.isEmail
import com.harian.closer.share.location.utils.extension.navigateWithAnimation
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.net.HttpURLConnection
import javax.inject.Inject
import kotlin.system.exitProcess


@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_login

    @Inject
    lateinit var appManager: AppManager

    private val viewModel by viewModels<LoginViewModel>()

    var loginState: State = LoginState(this)
    val enterEmailState: State = ResetPasswordState.EnterEmailState(this)
    val setNewPasswordState: State = ResetPasswordState.SetNewPasswordState(this)
    val registerState: State = RegisterState(this)
    val resetPasswordVerificationState: State = ResetPasswordState.VerificationState(this)
    private val verificationState: State = VerificationState(this)

    private var state: State = loginState

    override fun setupUI() {
        super.setupUI()
        state.setupUI()
        handleOnBackPressed()
        handleStateChanges()
    }

    override fun setupListener() {
        super.setupListener()
        state.setupListener()
        setClickRightIconEditText(binding.edtPassword)
        setClickRightIconEditText(binding.edtConfirmPassword)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setClickRightIconEditText(editText: EditText) {
        editText.apply {
            setOnTouchListener(OnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= right - totalPaddingEnd) {
                        transformationMethod = if (transformationMethod == null) PasswordTransformationMethod() else null
                        setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            if (transformationMethod == null) R.drawable.ic_eye
                            else R.drawable.ic_eye_closed,
                            0
                        )
                        return@OnTouchListener true
                    }
                }
                false
            })
        }

    }

    private fun handleOnBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (appManager.isBackPressFinish) {
                    activity?.finishAffinity()
                    exitProcess(0)
                } else {
                    showToast(getString(R.string.press_back_again_to_quit))
                }
            }
        })
    }

    private fun handleStateChanges() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                when (state) {
                    is LoginViewModel.FunctionState.Init -> Unit
                    is LoginViewModel.FunctionState.IsLoading -> handleLoading(state.isLoading)
                    is LoginViewModel.FunctionState.SuccessLogin -> handleSuccessLogin()
                    is LoginViewModel.FunctionState.ErrorLogin -> handleErrorLogin()
                    is LoginViewModel.FunctionState.SuccessRegister -> handleSuccessRegister()
                    is LoginViewModel.FunctionState.ErrorRegister -> handleErrorRegister(state.rawResponse)
                    is LoginViewModel.FunctionState.ErrorRequestOtp -> handleErrorRequestOtp()
                    is LoginViewModel.FunctionState.SuccessRequestOtp -> handleSuccessRequestOtp()
                    else -> Unit
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun handleSuccessRegister() {
        showToast(getString(R.string.register_successful))
        findNavController().navigateWithAnimation(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
    }

    private fun handleErrorRegister(rawResponse: WrappedResponse<RegisterResponse>) {
        if (rawResponse.code == HttpURLConnection.HTTP_CONFLICT) {
            showToast(getString(R.string.an_account_has_already_registered_with_this_email_please_use_another))
        } else {
            showToast(getString(R.string.register_failed_please_check_your_otp))
        }
        state.setupUI()
    }

    private fun handleErrorLogin() {
        showToast(getString(R.string.login_failed_wrong_password), Toast.LENGTH_LONG)
        state.setupUI()
    }

    private fun handleSuccessRequestOtp() {
        showToast(getString(R.string.an_otp_is_being_sent_to_you_please_check_spam_email))
        setState(verificationState)
    }

    private fun handleErrorRequestOtp() {
        showToast(getString(R.string.something_go_wrong_please_try_again))
    }

    private fun handleLoading(isLoading: Boolean) {
        binding.apply {
            context?.let {
                val textColor = ContextCompat.getColor(it, if (isLoading) R.color.transparent else R.color.white)
                callToActionBtn.setTextColor(textColor)
            }
            callToActionBtn.isEnabled = !isLoading
            loadingAnimation.isVisible = isLoading
        }
    }

    private fun handleSuccessLogin() {
        showToast(getString(R.string.login_successful))
        findNavController().navigateWithAnimation(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
    }

    fun login() {
        validateInput { email, password ->
            viewModel.login(
                LoginRequest(
                    email = email,
                    password = password
                )
            )
        }
    }

    fun requestOtpForRegister() {
        validateInput { email, password ->
            if (binding.edtConfirmPassword.text.toString() == password) {
                viewModel.requestOtp(RequestOtpRequest(email))
            } else {
                showToast("Confirmation password is incorrect")
            }
        }
    }

    fun register() {
        validateInput { email, password ->
            viewModel.register(
                RegisterRequest(
                    name = if (!binding.edtName.text.isNullOrBlank()) binding.edtName.text.toString() else "",
                    email = email,
                    password = password,
                    otp = binding.edtConfirmationCode.text.toString()
                )
            )
        }
    }

    private fun validateInput(onAccepted: (email: String, password: String) -> Unit) {
        if (isEmailValid()) {
            if (isPasswordValid()) {
                onAccepted.invoke(
                    binding.edtEmail.text.toString(),
                    binding.edtPassword.text.toString()
                )
            } else {
                showToast(getString(R.string.password_invalid_warning))
            }
        } else {
            showToast(getString(R.string.please_enter_a_valid_email))
        }
    }

    private fun isEmailValid(): Boolean {
        return !binding.edtEmail.text.isNullOrBlank() && binding.edtEmail.text.toString().isEmail()
    }

    private fun isPasswordValid(): Boolean {
        return !binding.edtPassword.text.isNullOrBlank() &&
                binding.edtEmail.text.toString().length > 8
    }

    fun setState(state: State) {
        this.state = state
        this.state.setupUI()
        this.state.setupListener()
    }

    fun getBinding() = binding
}

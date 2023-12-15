package com.harian.closer.share.location.presentation.login

import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.harian.closer.share.location.R
import com.harian.closer.share.location.data.login.remote.dto.LoginRequest
import com.harian.closer.share.location.data.register.remote.dto.RegisterRequest
import com.harian.closer.share.location.databinding.FragmentLoginBinding
import com.harian.closer.share.location.domain.login.entity.LoginEntity
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.platform.SharedPrefs
import com.harian.closer.share.location.presentation.login.state.LoginState
import com.harian.closer.share.location.presentation.login.state.ResetPasswordState
import com.harian.closer.share.location.presentation.login.state.RegisterState
import com.harian.closer.share.location.presentation.login.state.State
import com.harian.closer.share.location.presentation.login.state.VerificationState
import com.harian.closer.share.location.utils.extension.isEmail
import com.harian.closer.share.location.utils.extension.navigateWithAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.system.exitProcess

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_login

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private val viewModel by viewModels<LoginViewModel>()

    var loginState: State = LoginState(this)
    val enterEmailState: State = ResetPasswordState.EnterEmailState(this)
    val setNewPasswordState: State = ResetPasswordState.SetNewPasswordState(this)
    val registerState: State = RegisterState(this)
    val verificationState: State = VerificationState(this)

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
    }

    private fun handleOnBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finishAffinity()
                exitProcess(0)
            }
        })
    }

    private fun handleStateChanges() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                when (state) {
                    is LoginViewModel.UIState.Init -> Unit
                    is LoginViewModel.UIState.IsLoading -> handleLoading(state.isLoading)
                    is LoginViewModel.UIState.SuccessLogin -> handleSuccessLogin(state.loginEntity)
                    is LoginViewModel.UIState.ErrorLogin -> Unit
                    is LoginViewModel.UIState.SuccessRegister -> Unit
                    is LoginViewModel.UIState.ErrorRegister -> Unit
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun handleLoading(isLoading: Boolean) {
        binding.apply {
            callToActionBtn.text = ""
            callToActionBtn.isEnabled = !isLoading
            loadingAnimation.isVisible = isLoading
        }
    }

    private fun handleSuccessLogin(loginEntity: LoginEntity) {
        sharedPrefs.saveToken(loginEntity.token)
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

    fun register() {
        validateInput { email, password ->
            if (binding.edtConfirmPassword.text.toString() == password) {
                viewModel.register(
                    RegisterRequest(
                        name = if (!binding.edtName.text.isNullOrBlank()) binding.edtName.text.toString() else "",
                        email = email,
                        password = password
                    )
                )
            } else {
                showToast("Confirmation password is incorrect")
            }
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

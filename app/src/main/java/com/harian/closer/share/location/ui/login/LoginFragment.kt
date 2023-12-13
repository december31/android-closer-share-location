package com.harian.closer.share.location.ui.login

import com.harian.closer.share.location.R
import com.harian.closer.share.location.databinding.FragmentLoginBinding
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.ui.login.state.LoginState
import com.harian.closer.share.location.ui.login.state.ResendOTPState
import com.harian.closer.share.location.ui.login.state.ResetPasswordState
import com.harian.closer.share.location.ui.login.state.SignUpState
import com.harian.closer.share.location.ui.login.state.State
import com.harian.closer.share.location.ui.login.state.VerificationState

class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_login

    fun getBinding() = binding

    private var loginState: State = LoginState(this)
    val resendOTPState: State = ResendOTPState(this)
    val resetPasswordState: State = ResetPasswordState(this)
    val signUpState: State = SignUpState(this)
    val verificationSignUpState: State = VerificationState(this)

    private var state: State = loginState

    override fun setupUI() {
        super.setupUI()
        state.setupUI()
    }

    override fun setupListener() {
        super.setupListener()
        state.setupListener()
    }

    fun setState(state: State) {
        this.state = state
        setupUI()
        setupListener()
    }
}

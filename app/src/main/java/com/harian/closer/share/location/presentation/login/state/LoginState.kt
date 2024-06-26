package com.harian.closer.share.location.presentation.login.state

import com.harian.closer.share.location.presentation.login.LoginFragment
import com.harian.closer.share.location.utils.extension.goneAllChildView
import com.harian.closer.share.location.utils.extension.visible
import com.harian.software.closer.share.location.R

class LoginState(private val fragment: LoginFragment) : State {
    override fun setupUI() {
        fragment.getBinding().apply {
            loginContainer.goneAllChildView()

            edtEmail.visible()
            edtPassword.visible()
            tvForgotPassword.visible()
            tvOrLoginBy.visible()
            callToActionBtn.visible()
            callToActionBtn.setText(R.string.login)
            tvOrLoginBy.visible()
            imgGoogle.visible()
            imgFacebook.visible()
            tvLoginSignup.visible()
            tvLoginSignup.setText(R.string.sign_up)
            tvAlreadyHadAccount.visible()
            tvAlreadyHadAccount.setText(R.string.don_t_have_account)
        }
    }

    override fun setupListener() {
        fragment.getBinding().apply {
            tvLoginSignup.setOnClickListener {
                fragment.setState(fragment.registerState)
            }
            callToActionBtn.setOnClickListener {
                fragment.authenticate()
            }
            tvForgotPassword.setOnClickListener {
                fragment.setState(fragment.enterEmailState)
            }
        }
    }
}

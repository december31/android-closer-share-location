package com.harian.closer.share.location.presentation.login.state

import com.harian.closer.share.location.R
import com.harian.closer.share.location.presentation.login.LoginFragment
import com.harian.closer.share.location.utils.extension.goneAllChildView
import com.harian.closer.share.location.utils.extension.visible

class RegisterState (private val fragment: LoginFragment): State{
    override fun setupUI() {
        fragment.getBinding().apply {
            loginContainer.goneAllChildView()

            tvBack.visible()
            edtEmail.visible()
            edtPassword.visible()
            edtName.visible()
            edtConfirmPassword.visible()
            callToActionBtn.visible()
            containerLoginSignup.visible()
            callToActionBtn.setText(R.string.sign_up)
            tvAlreadyHadAccount.visible()
            tvAlreadyHadAccount.setText(R.string.already_have_account)
            tvLoginSignup.setText(R.string.login)
            tvLoginSignup.visible()
        }
    }

    override fun setupListener() {
        fragment.getBinding().apply {
            tvLoginSignup.setOnClickListener {
                fragment.setState(fragment.loginState)
            }
            tvBack.setOnClickListener {
                fragment.setState(fragment.loginState)
            }
            callToActionBtn.setOnClickListener {
                fragment.register()
            }
        }
    }

}

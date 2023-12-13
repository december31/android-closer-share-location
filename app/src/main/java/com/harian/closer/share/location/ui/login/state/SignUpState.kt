package com.harian.closer.share.location.ui.login.state

import android.view.ViewGroup.MarginLayoutParams
import com.harian.closer.share.location.R
import com.harian.closer.share.location.ui.login.LoginFragment
import com.harian.closer.share.location.utils.extension.goneAllChildView
import com.harian.closer.share.location.utils.extension.visible

class SignUpState (private val fragment: LoginFragment): State{
    override fun setupUI() {
        fragment.getBinding().apply {
            loginContainer.goneAllChildView()

            tvBack.visible()
            edtEmail.visible()
            edtPassword.visible()
            edtConfirmPassword.visible()
            btnLogin.visible()
            containerLoginSignup.visible()
            btnLogin.setText(R.string.sign_up)
            tvAlreadyHadAccount.visible()
            tvAlreadyHadAccount.setText(R.string.already_have_account)
            tvLoginSignup.setText(R.string.login)
            tvLoginSignup.visible()

            (containerLoginSignup.layoutParams as MarginLayoutParams).bottomMargin = 400
        }
    }

    override fun setupListener() {
        fragment.getBinding().apply {
            tvLoginSignup.setOnClickListener {
                (containerLoginSignup.layoutParams as MarginLayoutParams).bottomMargin = 0
                fragment.setState(fragment.loginState)
            }
            tvBack.setOnClickListener {
                (containerLoginSignup.layoutParams as MarginLayoutParams).bottomMargin = 0
                fragment.setState(fragment.loginState)
            }
        }
    }

}

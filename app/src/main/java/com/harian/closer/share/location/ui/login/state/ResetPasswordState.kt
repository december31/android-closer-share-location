package com.harian.closer.share.location.ui.login.state

import com.harian.closer.share.location.R
import com.harian.closer.share.location.ui.login.LoginFragment
import com.harian.closer.share.location.utils.extension.gone
import com.harian.closer.share.location.utils.extension.goneAllChildView
import com.harian.closer.share.location.utils.extension.invisible
import com.harian.closer.share.location.utils.extension.visible

class ResetPasswordState {
    class EnterEmailState(private val fragment: LoginFragment) : State {
        override fun setupUI() {
            fragment.getBinding().apply {
                loginContainer.goneAllChildView()

                tvBack.visible()
                tvTitle.setText(R.string.type_your_email)
                tvTitle.visible()
                tvDescription.setText(R.string.we_will_send_you_instruction_on_how_to_reset_your_password)
                tvDescription.visible()
                edtEmail.visible()
                btnLogin.setText(R.string.send)
                btnLogin.visible()
                imgBottomDecorator.visible()
            }
        }

        override fun setupListener() {
            fragment.getBinding().apply {
                btnLogin.setOnClickListener {
                    fragment.setState(fragment.setNewPasswordState)
                }
                tvBack.setOnClickListener {
                    fragment.setState(fragment.loginState)
                }
            }
        }
    }

    class SetNewPasswordState(private val fragment: LoginFragment): State {
        override fun setupUI() {
            fragment.getBinding().apply {
                loginContainer.goneAllChildView()

                tvBack.visible()
                edtPassword.visible()
                edtConfirmPassword.visible()
                tvTitle.setText(R.string.set_new_password)
                tvTitle.visible()
                tvDescription.setText(R.string.type_your_new_password)
                tvDescription.visible()
                edtEmail.invisible()
                edtPassword.visible()
                btnLogin.setText(R.string.send)
                btnLogin.visible()
                imgBottomDecorator.visible()
            }
        }

        override fun setupListener() {

        }
    }
}

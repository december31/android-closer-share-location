package com.harian.closer.share.location.presentation.login.state

import com.harian.closer.share.location.R
import com.harian.closer.share.location.presentation.login.LoginFragment
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
                callToActionBtn.setText(R.string.send)
                callToActionBtn.visible()
                imgBottomDecorator.visible()
            }
        }

        override fun setupListener() {
            fragment.getBinding().apply {
                callToActionBtn.setOnClickListener {
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
                callToActionBtn.setText(R.string.send)
                callToActionBtn.visible()
                imgBottomDecorator.visible()
            }
        }

        override fun setupListener() {
            fragment.getBinding().apply {
                callToActionBtn.setOnClickListener {
                }
            }
        }
    }
    class VerificationState (private val fragment: LoginFragment): State{
        override fun setupUI() {
            fragment.getBinding().apply {
                loginContainer.goneAllChildView()

                tvBack.visible()
                tvTitle.visible()
                tvTitle.setText(R.string.verification)
                tvDescription.visible()
                tvDescription.setText(R.string.a_message_with_verification_code_was_sent_to_your_mobile_phone)
                edtConfirmationCode.visible()
                tvNotReceiveCode.visible()
                imgBottomDecorator.visible()
                callToActionBtn.setText(R.string.verify)
                callToActionBtn.visible()
            }
        }

        override fun setupListener() {
            fragment.getBinding().apply {
                callToActionBtn.setOnClickListener {
                    fragment.register()
                }
                tvNotReceiveCode.setOnClickListener {

                }
                tvBack.setOnClickListener {
                    fragment.setState(fragment.enterEmailState)
                }
            }
        }
    }
}

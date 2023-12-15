package com.harian.closer.share.location.presentation.login.state

import com.harian.closer.share.location.R
import com.harian.closer.share.location.presentation.login.LoginFragment
import com.harian.closer.share.location.utils.extension.goneAllChildView
import com.harian.closer.share.location.utils.extension.visible

class VerificationState (private val fragment: LoginFragment): State{
    override fun setupUI() {
        fragment.getBinding().apply {
            loginContainer.goneAllChildView()

            tvBack.visible()
            tvTitle.visible()
            tvTitle.setText(R.string.verification)
            tvDescription.visible()
            tvDescription.setText(R.string.a_message_with_verification_code_was_sent_to_your_mobile_phone)
            edtConfirmCode.visible()
            tvNotReceiveCode.visible()
            imgBottomDecorator.visible()
            callToActionBtn.setText(R.string.verify)
            callToActionBtn.visible()
        }
    }

    override fun setupListener() {
        fragment.getBinding().apply {
            callToActionBtn.setOnClickListener {

            }
            tvNotReceiveCode.setOnClickListener {

            }
            tvBack.setOnClickListener {
                fragment.setState(fragment.registerState)
            }
        }
    }
}

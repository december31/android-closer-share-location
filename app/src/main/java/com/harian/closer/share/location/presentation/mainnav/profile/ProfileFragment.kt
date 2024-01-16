package com.harian.closer.share.location.presentation.mainnav.profile

import android.content.Context
import android.telephony.TelephonyManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.harian.closer.share.location.domain.country.entity.CountryEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.utils.extension.glideLoadImage
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_profile

    private val viewModel: ProfileViewModel by viewModels()

    override fun setupUI() {
        super.setupUI()
        handleStateChanges()
        viewModel.getUserInformation()
    }

    private fun handleStateChanges() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).onEach {
            when (it) {
                is ProfileViewModel.ProfileState.Init -> Unit
                is ProfileViewModel.ProfileState.ErrorGetUserInformation -> handleErrorGetUserInformation()
                is ProfileViewModel.ProfileState.SuccessGetUserInformation -> handleSuccessGetUserInformation(it.user)
                is ProfileViewModel.ProfileState.ErrorGetCountry -> Unit
                is ProfileViewModel.ProfileState.SuccessGetCountry -> handleSuccessGetCountry(it.country)
            }
        }.launchIn(lifecycleScope)
    }

    private fun handleSuccessGetCountry(country: CountryEntity) {
//        binding.address.text = country.name
    }

    private fun handleErrorGetUserInformation() {

    }

    private fun handleSuccessGetUserInformation(user: UserEntity) {
        binding.apply {
//            username.text = user.name
//            avatar.glideLoadImage(user.authorizedAvatarUrl)

//            val tm = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//            val countryCode = tm.networkCountryIso
//            viewModel.getCountry(countryCode)
        }
    }
}

package com.harian.closer.share.location.presentation.mainnav.profile

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.harian.closer.share.location.domain.country.entity.CountryEntity
import com.harian.closer.share.location.domain.user.entity.UserEntity
import com.harian.closer.share.location.platform.BaseFragment
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
    private lateinit var adapter: ProfileAdapter

    override fun setupUI() {
        super.setupUI()
        setupRecyclerView()
        handleStateChanges()
        viewModel.getUserInformation()
    }

    private fun setupRecyclerView() {
        adapter = ProfileAdapter(viewModel.sharedPrefs.getToken())
        binding.rv.adapter = adapter
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
        adapter.updateData(listOf(user))
    }
}

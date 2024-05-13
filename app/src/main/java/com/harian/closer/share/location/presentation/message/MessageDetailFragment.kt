package com.harian.closer.share.location.presentation.message

import androidx.core.view.WindowCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.platform.SharedPrefs
import com.harian.closer.share.location.utils.extension.findGlobalNavController
import com.harian.closer.share.location.utils.extension.glideLoadImage
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentMessageDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MessageDetailFragment : BaseFragment<FragmentMessageDetailBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_message_detail

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private val args by navArgs<MessageDetailFragmentArgs>()
    private val viewmodel by viewModels<MessageViewModel>()

    private val messagesAdapter = MessageDetailAdapter()

    override fun setupSystemBarBehavior() {
        super.setupSystemBarBehavior()
        activity?.window?.let { WindowCompat.setDecorFitsSystemWindows(it, true) }
    }

    override fun setupUI() {
        super.setupUI()
        binding.apply {
            args.user.let { user ->
                imgAvatar.glideLoadImage(user.getAuthorizedAvatarUrl(sharedPrefs.getToken()))
                tvName.text = user.name
            }
        }
        setupRecyclerView()
        handleStateChanges()
        viewmodel.getMessage(args.user)
        viewmodel.subscribeMessage()
    }

    private fun setupRecyclerView() {
        binding.apply {
            rvMessages.adapter = messagesAdapter
        }
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            btnSend.setOnClickListener {
                validateMessage { message: String ->
                    viewmodel.sendMessage(args.user, message)
                    edtMessage.setText("")
                }
            }
            btnBack.setOnClickListener {
                findGlobalNavController()?.popBackStack()
            }
        }
    }

    private fun handleStateChanges() {
        viewmodel.messagesLiveData.observe(viewLifecycleOwner) {
            messagesAdapter.updateData(it)
            binding.rvMessages.scrollToPosition(0)
        }
    }

    private fun validateMessage(onValid: (message: String) -> Unit) {
        if (!binding.edtMessage.text.isNullOrBlank()) {
            onValid.invoke(binding.edtMessage.text.toString())
        } else {
            showToast(getString(R.string.message_is_empty))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.window?.let { WindowCompat.setDecorFitsSystemWindows(it, false) }
    }
}

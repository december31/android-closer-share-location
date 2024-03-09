package com.harian.closer.share.location.presentation.imagesviewer

import androidx.navigation.fragment.navArgs
import com.harian.closer.share.location.platform.BaseFragment
import com.harian.closer.share.location.platform.SharedPrefs
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.FragmentImagesViewerBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ImagesViewerFragment : BaseFragment<FragmentImagesViewerBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_images_viewer

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private val args by navArgs<ImagesViewerFragmentArgs>()

    override fun setupUI() {
        super.setupUI()
        val adapter = ImagesViewerAdapter(sharedPrefs.getToken())
        binding.viewPager.adapter = adapter
        adapter.updateData(args.images.toList())
    }
}

package com.harian.closer.share.location.presentation.homenav.map

import com.google.android.gms.maps.GoogleMap
import com.harian.closer.share.location.platform.BaseBottomSheetDialogFragment
import com.harian.closer.share.location.platform.SharedPrefs
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.BottomSheetDialogMapTypeBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MapTypeBottomSheetDialog : BaseBottomSheetDialogFragment<BottomSheetDialogMapTypeBinding>() {

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    var onSelectMapType: (Int) -> Unit = {}

    override val layoutId: Int
        get() = R.layout.bottom_sheet_dialog_map_type

    override fun setupUI() {
        super.setupUI()
        binding.mapType = sharedPrefs.getMapType()
    }

    override fun setupListener() {
        super.setupListener()
        binding.apply {
            rltNormal.setOnClickListener {
                onSelectMapType(GoogleMap.MAP_TYPE_NORMAL)
                sharedPrefs.setMapType(GoogleMap.MAP_TYPE_NORMAL)
            }
            rltSatellite.setOnClickListener {
                onSelectMapType(GoogleMap.MAP_TYPE_SATELLITE)
                sharedPrefs.setMapType(GoogleMap.MAP_TYPE_SATELLITE)
            }
            rltTerrain.setOnClickListener {
                onSelectMapType(GoogleMap.MAP_TYPE_TERRAIN)
                sharedPrefs.setMapType(GoogleMap.MAP_TYPE_TERRAIN)
            }
            btnClose.setOnClickListener {
                dismiss()
            }
        }
    }
}

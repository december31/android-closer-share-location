package com.harian.closer.share.location.utils.extension

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.navOptions
import com.harian.software.closer.share.location.R

fun NavController.navigateWithAnimation(directions: NavDirections) {
    navigate(directions, navOptions {
        anim {
            enter = R.anim.fade_in
            exit = R.anim.fade_out
            popEnter = R.anim.pop_fade_in
            popExit = R.anim.fade_out
        }
    })
}

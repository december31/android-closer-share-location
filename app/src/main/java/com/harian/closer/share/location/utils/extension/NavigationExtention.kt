package com.harian.closer.share.location.utils.extension

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.navOptions
import com.harian.software.closer.share.location.R

fun NavController.navigateWithAnimation(directions: NavDirections, animation: Animation = Animation.Zoom) {
    navigate(directions, animation.getNavOptions())
}

sealed class Animation {
    abstract fun getNavOptions(): NavOptions

    data object Zoom : Animation() {
        override fun getNavOptions(): NavOptions {
            return navOptions {
                anim {
                    enter = R.anim.fade_in
                    popExit = R.anim.fade_out
                    exit = R.anim.fade_out
                    popEnter = R.anim.pop_fade_in
                }
            }
        }
    }

    data object SlideUp : Animation() {
        override fun getNavOptions(): NavOptions {
            return navOptions {
                anim {
                    enter = R.anim.slide_up
                    popExit = R.anim.slide_down
                    exit = R.anim.fade_out
                    popEnter = R.anim.pop_slide_down
                }
            }
        }
    }
}

fun Fragment.findMainNavController(): NavController? {
    if (activity == null) return null
    return Navigation.findNavController(activity!!, R.id.nav_host_fragment_content_main)
}

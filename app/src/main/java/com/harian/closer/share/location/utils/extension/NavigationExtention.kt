package com.harian.closer.share.location.utils.extension

import android.os.Bundle
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

fun NavController.navigateWithAnimation(resId: Int, animation: Animation = Animation.Zoom, bundle: Bundle? = null) {
    navigate(resId, bundle, animation.getNavOptions())
}

fun NavController.isOnBackStack(resId: Int): Boolean {
    return try {
        getBackStackEntry(resId)
        true
    } catch (e: IllegalArgumentException) {
        false
    }
}

sealed class Animation {
    abstract fun getNavOptions(): NavOptions

    data object Zoom : Animation() {
        override fun getNavOptions(): NavOptions {
            return navOptions {
                anim {
                    enter = R.anim.zoom_fade_in
                    exit = R.anim.zoom_fade_out
                    popExit = R.anim.zoom_fade_out
                    popEnter = R.anim.pop_zoom_fade_in
                }
            }
        }
    }

    data object SlideUp : Animation() {
        override fun getNavOptions(): NavOptions {
            return navOptions {
                anim {
                    enter = R.anim.slide_up
                    exit = R.anim.zoom_fade_out
                    popEnter = R.anim.pop_slide_down
                    popExit = R.anim.slide_down
                }
            }
        }
    }

    data object FadeIn : Animation() {
        override fun getNavOptions(): NavOptions {
            return navOptions {
                anim {
                    enter = R.anim.fade_in
                    exit = R.anim.fade_out
                    popEnter = R.anim.pop_slide_down
                    popExit = R.anim.fade_out
                }
            }
        }
    }

    data object SlideLeft : Animation() {
        override fun getNavOptions(): NavOptions {
            return navOptions {
                anim {
                    enter = R.anim.slide_left_enter
                    exit = R.anim.slide_left_exit
                    popEnter = R.anim.slide_right_enter
                    popExit = R.anim.slide_right_exit
                }
            }
        }
    }

    data object SlideRight : Animation() {
        override fun getNavOptions(): NavOptions {
            return navOptions {
                anim {
                    enter = R.anim.slide_right_enter
                    exit = R.anim.slide_left_enter
                    popEnter = R.anim.slide_left_enter
                    popExit = R.anim.slide_left_enter
                }
            }
        }
    }
}

fun Fragment.findMainNavController(): NavController? {
    if (activity == null) return null
    return Navigation.findNavController(activity!!, R.id.nav_host_fragment_content_main)
}

fun Fragment.findHomeNavController(): NavController? {
    if (activity == null) return null
    return Navigation.findNavController(activity!!, R.id.home_nav_host_fragment)
}


package com.harian.closer.share.location

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.harian.closer.share.location.domain.post.entity.PostEntity
import com.harian.closer.share.location.presentation.notification.FirebaseNotificationService
import com.harian.closer.share.location.presentation.notification.NotificationModel
import com.harian.software.closer.share.location.R
import com.harian.software.closer.share.location.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleIntent()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null && currentFocus !is EditText) {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun handleIntent() {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        intent?.getStringExtra(FirebaseNotificationService.KEY_NOTIFICATION_TYPE)?.let {
            val notificationType = NotificationModel.Type.valueOf(it)
            when (notificationType) {
                NotificationModel.Type.FRIEND_REQUEST -> Unit
                NotificationModel.Type.MESSAGE -> Unit
                NotificationModel.Type.POST -> {
                    val model = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(FirebaseNotificationService.KEY_NOTIFICATION_DATA, PostEntity::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(FirebaseNotificationService.KEY_NOTIFICATION_DATA)
                    }

                    model?.id?.let {
                        navController.navigate(R.id.post_details_fragment, bundleOf("postId" to it))
                    }
                }
            }
        }

    }
}

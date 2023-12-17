package com.harian.closer.share.location.utils.extension

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.harian.software.closer.share.location.R

fun Context.showToast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showGenericAlertDialog(message: String){
    AlertDialog.Builder(this).apply {
        setMessage(message)
        setPositiveButton(getString(R.string.button_text_ok)){ dialog, _ ->
             dialog.dismiss()
        }
    }.show()
}

fun Activity.hideSoftKeyboard(view: View) {
    currentFocus?.let {
        val inputMethodManager = ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

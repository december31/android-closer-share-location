package com.harian.closer.share.location.utils.extension

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.harian.closer.share.location.R

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

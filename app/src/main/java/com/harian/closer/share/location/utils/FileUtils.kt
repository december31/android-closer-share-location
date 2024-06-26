package com.harian.closer.share.location.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


class FileUtils {
    companion object {
        @Throws(IOException::class)
        fun getFile(context: Context, uri: Uri): File {
            val destinationFilename = File(context.filesDir.path + File.separatorChar + queryName(context, uri))
            try {
                context.contentResolver.openInputStream(uri).use { ins ->
                    if (ins != null) {
                        createFileFromStream(ins, destinationFilename)
                    }
                }
            } catch (ex: Exception) {
                Log.e("Save File", ex.message.toString())
                ex.printStackTrace()
            }
            return destinationFilename
        }

        fun createFileFromStream(ins: InputStream, destination: File?) {
            try {
                FileOutputStream(destination).use { os ->
                    val buffer = ByteArray(4096)
                    var length: Int
                    while (ins.read(buffer).also { length = it } > 0) {
                        os.write(buffer, 0, length)
                    }
                    os.flush()
                }
            } catch (ex: java.lang.Exception) {
                Log.e("Save File", ex.message.toString())
                ex.printStackTrace()
            }
        }

        private fun queryName(context: Context, uri: Uri): String? {
            val returnCursor = context.contentResolver.query(uri, null, null, null, null)!!
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            val name = returnCursor.getString(nameIndex)
            returnCursor.close()
            return name
        }

    }
}

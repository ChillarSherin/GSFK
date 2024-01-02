package com.chillarcards.gsfk.utills

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Build
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.chillarcards.gsfk.R
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Const {
    companion object {


        const val ver_title = ":  " //Client

        fun enableButton(button: Button) {
            button.isEnabled = true
            button.alpha = 1f
        }

        fun disableButton(button: Button) {
            button.isEnabled = false
            button.alpha = 0.55f
        }

        fun shortToast(context: Context, value: String) {
            //Toast.makeText(context, value, Toast.LENGTH_SHORT).show()
            val builder = AlertDialog.Builder(context)
            //set message for alert dialog
            builder.setMessage(value)
            //performing negative action
            builder.setNegativeButton(context.getString(R.string.close)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setOnShowListener {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.resources.getColor(R.color.primary_red)
                )
            }
            alertDialog.setCanceledOnTouchOutside(false)
            alertDialog.show()
        }
        fun logout(context: Context, value: String) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(value)
            builder.setNegativeButton(context.getString(R.string.close)) { dialogInterface, _ ->
                clearCache(context)
                deleteCache(context)
                dialogInterface.dismiss()
            }
            val alertDialog: AlertDialog = builder.create()
             alertDialog.setOnShowListener {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.resources.getColor(R.color.primary_red)
                )
            }
            alertDialog.setCanceledOnTouchOutside(false)
            alertDialog.show()
        }

        fun getCurrentDate(): String {
            val currentDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDate.now()
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return currentDate.format(formatter)
        }

        // Function to clear cache
        fun clearCache(context: Context) {
            try {
                val cacheDir = context.cacheDir
                if (cacheDir.exists()) {
                    cacheDir.listFiles()?.forEach { file ->
                        file.delete()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun deleteCache(context: Context) {
            try {
                val dir = context.cacheDir
                deleteDir(dir)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun deleteDir(dir: File?): Boolean {
            return if (dir != null && dir.isDirectory) {
                val children = dir.list()
                for (i in children.indices) {
                    val success = deleteDir(File(dir, children[i]))
                    if (!success) {
                        return false
                    }
                }
                dir.delete()
            } else if (dir != null && dir.isFile) {
                dir.delete()
            } else {
                false
            }
        }

    }
}
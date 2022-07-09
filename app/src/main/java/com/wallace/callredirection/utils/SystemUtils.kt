package com.wallace.callredirection.utils

import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

object SystemUtils {
    fun isGreaterThanOrEqualsAndroidO() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    fun isGreaterThanOrEqualsAndroidP() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    fun isGreaterThanOrEqualsAndroidQ() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    fun isLessThanOrEqualsAndroidQ() = Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q

    fun AppCompatActivity.showAlertDialog(
        title: String,
        message: String,
        positiveButtonName: String,
        positiveButtonClick: () -> Unit,
        negativeButtonName: String,
        negativeButtonClick: () -> Unit,
    ) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(positiveButtonName) { _, _ ->
                positiveButtonClick.invoke()
            }
            setNegativeButton(negativeButtonName) { _, _ ->
                negativeButtonClick.invoke()
            }
        }.show()
    }
}
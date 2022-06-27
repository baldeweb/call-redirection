package com.wallace.callredirection

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.reflect.KClass

object SystemUtils {
    fun isGreaterThanOrEqualsAndroidO() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    fun isGreaterThanOrEqualsAndroidP() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    fun isGreaterThanOrEqualsAndroidQ() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

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

    fun Context.isServiceRunning(serviceClass: KClass<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        for (service in (manager?.getRunningServices(Int.MAX_VALUE) ?: listOf())) {
            if (serviceClass.simpleName == service.service.className) {
                return true
            }
        }
        return false
    }

    fun Context.isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        for (service in (manager?.getRunningServices(Int.MAX_VALUE) ?: listOf())) {
            if (serviceClass.simpleName == service.service.className) {
                return true
            }
        }
        return false
    }
}
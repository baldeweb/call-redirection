package com.wallace.callredirection

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionUtils {
    fun Context.hasReadPhoneStatePermission() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.READ_PHONE_STATE
    ) == PackageManager.PERMISSION_GRANTED

    fun Context.hasReadCallLogPermission() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.READ_CALL_LOG
    ) == PackageManager.PERMISSION_GRANTED
}
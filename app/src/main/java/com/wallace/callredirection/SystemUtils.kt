package com.wallace.callredirection

import android.os.Build

object SystemUtils {
    fun isGreaterThanOrEqualsAndroidO() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    fun isGreaterThanOrEqualsAndroidP() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    fun isGreaterThanOrEqualsAndroidQ() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
}
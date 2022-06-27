package com.wallace.callredirection

import android.Manifest.permission.ANSWER_PHONE_CALLS
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Context.TELECOM_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.telecom.TelecomManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.wallace.callredirection.SystemUtils.isGreaterThanOrEqualsAndroidP
import com.wallace.callredirection.SystemUtils.isGreaterThanOrEqualsAndroidQ
import kotlin.system.exitProcess


class AttendanceReceiver : BroadcastReceiver() {
    var onCallFinished: () -> Unit = {}
    var onRequestPermission: () -> Unit = {}

    override fun onReceive(ctx: Context?, intent: Intent?) {
        if (isGreaterThanOrEqualsAndroidQ()) {
            if (intent?.action == "ATTENDANCE_REDIRECT_FINISH_CALL") {
                intent.component = ComponentName(
                    "com.wallace.callredirection",
                    "com.wallace.callredirection.AttendanceActivity"
                )
                onCallFinished.invoke()
            }
        } else {
            val number = intent?.extras?.getString("incoming_number")
            Log.d("LOG", "[BroadcastReceiver] - PhoneNumber: $number")
            resultData = null
            onCallFinished.invoke()
            val tm: TelecomManager = ctx?.getSystemService(TELECOM_SERVICE) as TelecomManager
            if (ActivityCompat.checkSelfPermission(
                    ctx, ANSWER_PHONE_CALLS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                onRequestPermission.invoke()
            } else {
                if (isGreaterThanOrEqualsAndroidP()) {
                    tm.endCall()
                } else {
                    exitProcess(0)
                }
            }
        }
    }

}
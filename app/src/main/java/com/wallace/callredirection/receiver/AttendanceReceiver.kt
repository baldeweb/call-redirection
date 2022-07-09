package com.wallace.callredirection.receiver

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import com.wallace.callredirection.utils.Constants.ATTENDANCE_REDIRECT_ACTION
import com.wallace.callredirection.utils.Constants.OUTGOING_STATE
import com.wallace.callredirection.utils.Constants.TRIGGER_NUMBER
import com.wallace.callredirection.utils.SystemUtils.isGreaterThanOrEqualsAndroidQ

class AttendanceReceiver : BroadcastReceiver() {
    var onCallFinished: () -> Unit = {}
    var onRequestPermission: () -> Unit = {}

    override fun onReceive(ctx: Context?, intent: Intent?) {
        if (isGreaterThanOrEqualsAndroidQ()) {
            if (intent?.action == ATTENDANCE_REDIRECT_ACTION) {
                intent.component = ComponentName(
                    "com.wallace.callredirection",
                    "com.wallace.callredirection.ui.AttendanceActivity"
                )
                onCallFinished.invoke()
            }
        } else {
            val number = intent?.extras?.getString("incoming_number") ?: ""
            val state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE) ?: ""

            if (state == OUTGOING_STATE && number == TRIGGER_NUMBER) {
                Handler(Looper.getMainLooper()).postDelayed({
                    endCall(ctx)
                    redirectAttendance(ctx)
                }, 1000)
            }
        }
    }

    private fun endCall(ctx: Context?) {
        val telecomManager = ctx?.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        val endCallMethod = telecomManager.javaClass.getDeclaredMethod("endCall")
        endCallMethod.isAccessible = true
        endCallMethod.invoke(telecomManager)
    }

    private fun redirectAttendance(ctx: Context?) {
        ctx?.startActivity(Intent("android.navigation.attendance").apply {
            setPackage(ctx.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

}
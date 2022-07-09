package com.wallace.callredirection.receiver

import android.content.*
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.util.Log
import com.wallace.callredirection.utils.SystemUtils.isGreaterThanOrEqualsAndroidQ


class AttendanceReceiver : BroadcastReceiver() {
    var onCallFinished: () -> Unit = {}
    var onRequestPermission: () -> Unit = {}

    override fun onReceive(ctx: Context?, intent: Intent?) {
        if (isGreaterThanOrEqualsAndroidQ()) {
            if (intent?.action == "ATTENDANCE_REDIRECT_FINISH_CALL") {
                intent.component = ComponentName(
                    "com.wallace.callredirection",
                    "com.wallace.callredirection.ui.AttendanceActivity"
                )
                Log.d("LOG", "1 [BroadcastReceiver] - PhoneNumberIncoming")
                onCallFinished.invoke()
            }
        } else {
            val number = intent?.extras?.getString("incoming_number")
            val state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE) ?: ""

            Log.d("LOG", "2 [BroadcastReceiver] - number: $number")
            Log.d("LOG", "2 [BroadcastReceiver] - state: $state")

            if (state == "OFFHOOK" && !number.isNullOrEmpty())
                redirectAttendance(ctx)
        }
    }

    private fun redirectAttendance(ctx: Context?) {
        Handler(Looper.getMainLooper()).postDelayed({
            ctx?.startActivity(Intent("android.navigation.attendance").apply {
                setPackage(ctx.packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }, 1000)
    }

}
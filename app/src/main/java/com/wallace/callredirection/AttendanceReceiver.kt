package com.wallace.callredirection

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent

class AttendanceReceiver: BroadcastReceiver() {
    var onCallFinished: () -> Unit = {}
    override fun onReceive(ctx: Context?, intent: Intent?) {
        if (intent?.action == "ATTENDANCE_REDIRECT_FINISH_CALL") {
//            resultData = null
            
            intent.component = ComponentName(
                "com.wallace.callredirection",
                "com.wallace.callredirection.AttendanceActivity"
            )
            onCallFinished.invoke()
        }
    }
}
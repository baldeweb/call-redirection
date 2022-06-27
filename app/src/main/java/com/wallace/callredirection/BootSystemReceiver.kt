package com.wallace.callredirection

import android.app.role.RoleManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.Settings
import android.util.Log
import com.wallace.callredirection.PermissionUtils.hasReadPhoneStatePermission
import com.wallace.callredirection.SystemUtils.isGreaterThanOrEqualsAndroidQ
import com.wallace.callredirection.SystemUtils.isServiceRunning

class BootSystemReceiver: BroadcastReceiver() {
    private var receiver: AttendanceReceiver? = null
    var onRequestPermission: () -> Unit = {}
    var onCallFinished: () -> Unit = {}

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context?.hasReadPhoneStatePermission() == true) {
            startSystem(context)
        }
    }

    private fun startSystem(context: Context) {
        if (Settings.canDrawOverlays(context)) {
            startReceiver(context)
            startCallService(context)
        }
    }

    private fun startReceiver(context: Context) {
        if (!RoleCallRedirectionManager.isRedirection(context) && isGreaterThanOrEqualsAndroidQ()) {
            RoleCallRedirectionManager.requestCallRedirectionPermission(
                context, null, RoleManager.ROLE_CALL_REDIRECTION
            )
        }

        if (receiver == null) {
            receiver = AttendanceReceiver()
            context.registerReceiver(receiver, IntentFilter("ATTENDANCE_REDIRECT_FINISH_CALL").apply {
                addAction("android.intent.action.PHONE_STATE")
                addAction(Intent.ACTION_NEW_OUTGOING_CALL)
                priority = 1000
            })
            receiver?.onCallFinished = {
                Log.d("LOG", "onCallFinished")
                onCallFinished.invoke()
                context.startActivity(Intent("android.navigation.attendance").apply {
                    setPackage(context.packageName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
            receiver?.onRequestPermission = {
                if (SystemUtils.isGreaterThanOrEqualsAndroidO()) {
                    onRequestPermission.invoke()
                }
            }
        }
    }

    private fun startCallService(context: Context?) {
        when {
            isGreaterThanOrEqualsAndroidQ() -> {
                if (context?.isServiceRunning(NumberCallRedirectionService::class) == false) {
                    val intent = Intent(context, NumberCallRedirectionService::class.java)
                    context.startForegroundService(intent)
                }
            }
            else -> return
        }
    }
}
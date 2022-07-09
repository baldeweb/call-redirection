package com.wallace.callredirection.ui

import android.Manifest.permission.*
import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.wallace.callredirection.R
import com.wallace.callredirection.receiver.AttendanceReceiver
import com.wallace.callredirection.service.NumberCallRedirectionService
import com.wallace.callredirection.ui.RoleCallRedirectionManager.Companion.isRedirection
import com.wallace.callredirection.ui.RoleCallRedirectionManager.Companion.requestCallRedirectionPermission
import com.wallace.callredirection.utils.PermissionUtils.hasReadCallLogPermission
import com.wallace.callredirection.utils.PermissionUtils.hasReadPhoneStatePermission
import com.wallace.callredirection.utils.SystemUtils.isGreaterThanOrEqualsAndroidO
import com.wallace.callredirection.utils.SystemUtils.isGreaterThanOrEqualsAndroidQ
import com.wallace.callredirection.utils.SystemUtils.showAlertDialog


class MainActivity : AppCompatActivity() {
    private var receiver: AttendanceReceiver? = null

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("LOG", "ResultLauncher: OK | Data: ${result.data}")
        } else {
            Log.d("LOG", "ResultLauncher: NOK")
        }
    }

    private var requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.filter { it.value == true }.isNotEmpty()) {
            startSystem()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!hasReadPhoneStatePermission() || !hasReadCallLogPermission()) {
            if (isGreaterThanOrEqualsAndroidO()) {
                requestPermissionLauncher.launch(
                    arrayOf(
                        READ_CALL_LOG,
                        READ_PHONE_STATE,
                        ANSWER_PHONE_CALLS
                    )
                )
            } else {
                requestPermissionLauncher.launch(arrayOf(READ_CALL_LOG, READ_PHONE_STATE))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!hasReadPhoneStatePermission() || !hasReadCallLogPermission()) {
            if (isGreaterThanOrEqualsAndroidO()) {
                requestPermissionLauncher.launch(
                    arrayOf(
                        READ_CALL_LOG,
                        READ_PHONE_STATE,
                        ANSWER_PHONE_CALLS
                    )
                )
            } else {
                requestPermissionLauncher.launch(arrayOf(READ_CALL_LOG, READ_PHONE_STATE))
            }
        } else {
            startSystem()
        }
    }

    private fun startSystem() {
        if (!Settings.canDrawOverlays(this)) {
            showAlertDialog(
                "Precisamos de sua permissão",
                "O motivo é porque sim, aprova logo ai.",
                "ok", {
                    navigateSettingsPermission()
                },
                "no, thanks", {
                    return@showAlertDialog
                }
            )
        } else {
            startAttendanceReceiver()
            startCallService()
        }
    }

    private fun startCallService() {
        when {
            isGreaterThanOrEqualsAndroidQ() -> {
                val intent = Intent(this, NumberCallRedirectionService::class.java)
                startService(intent)
            }
            else -> return
        }
    }

    private fun navigateSettingsPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        resultLauncher.launch(intent)
    }

    private fun startAttendanceReceiver() {
        if (!isRedirection(this) && isGreaterThanOrEqualsAndroidQ()) {
            requestCallRedirectionPermission(
                this,
                resultLauncher,
                RoleManager.ROLE_CALL_REDIRECTION
            )
        }

        if (receiver == null) {
            receiver = AttendanceReceiver()
            registerReceiver(receiver, IntentFilter("ATTENDANCE_REDIRECT_FINISH_CALL").apply {
                addAction("android.intent.action.PHONE_STATE")
                addAction(Intent.ACTION_NEW_OUTGOING_CALL)
                priority = 1000
            })
            receiver?.onCallFinished = {
                Log.d("LOG", "[AttendanceReceiver] onCallFinished")
                startActivity(Intent("android.navigation.attendance").apply {
                    setPackage(packageName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
            receiver?.onRequestPermission = {
                if (isGreaterThanOrEqualsAndroidO()) {
                    requestPermissionLauncher.launch(arrayOf(ANSWER_PHONE_CALLS))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
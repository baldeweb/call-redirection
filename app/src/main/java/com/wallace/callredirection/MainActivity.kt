package com.wallace.callredirection

import android.Manifest.permission.ANSWER_PHONE_CALLS
import android.Manifest.permission.READ_PHONE_STATE
import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.wallace.callredirection.PermissionUtils.hasReadPhoneStatePermission
import com.wallace.callredirection.SystemUtils.isGreaterThanOrEqualsAndroidO
import com.wallace.callredirection.SystemUtils.isGreaterThanOrEqualsAndroidQ


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
        if (!permissions.filter { it.value == true }.isNullOrEmpty()) {
            startSystem()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        if (!hasReadPhoneStatePermission()) {
            if (isGreaterThanOrEqualsAndroidO()) {
                requestPermissionLauncher.launch(arrayOf(READ_PHONE_STATE, ANSWER_PHONE_CALLS))
            } else {
                requestPermissionLauncher.launch(arrayOf(READ_PHONE_STATE))
            }
        } else {
            startSystem()
        }
    }

    private fun startSystem() {
        if (!Settings.canDrawOverlays(this)) {
            showPermissionDialog()
        } else {
            startReceiver()
            startCallService()
        }
    }

    private fun startCallService() {
        when {
            isGreaterThanOrEqualsAndroidQ() -> {
                val intent = Intent(this, NumberCallRedirectionService::class.java)
                startForegroundService(intent)
            }
            else -> return
        }
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Precisamos de sua permissão")
            setMessage("O motivo é porque sim, aprova logo ai.")
            setPositiveButton("ok") { _, _ ->
                navigateSettingsPermission()
            }
            setNegativeButton("No, thanks") { _, _ ->
                return@setNegativeButton
            }
        }.show()
    }

    private fun navigateSettingsPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        resultLauncher.launch(intent)
    }

    private fun startReceiver() {
        if (!isRedirection() && isGreaterThanOrEqualsAndroidQ()) {
            requestCallRedirectionPermission(RoleManager.ROLE_CALL_REDIRECTION)
        }

        receiver = AttendanceReceiver()
        registerReceiver(receiver, IntentFilter("ATTENDANCE_REDIRECT_FINISH_CALL").apply {
            addAction("android.intent.action.PHONE_STATE")
            addAction(Intent.ACTION_NEW_OUTGOING_CALL)
            priority = 1000
        })
        receiver?.onCallFinished = {
            Log.d("LOG", "onCallFinished")
            runOnUiThread {
                startActivity(Intent("android.navigation.attendance").apply {
                    setPackage(packageName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        }
    }

    private fun isRedirection(): Boolean {
        return if (isGreaterThanOrEqualsAndroidQ()) {
            isRoleHeldByApp(RoleManager.ROLE_CALL_REDIRECTION)
        } else {
            false
        }
    }

    private fun isRoleHeldByApp(roleName: String): Boolean {
        val roleManager: RoleManager?
        if (isGreaterThanOrEqualsAndroidQ()) {
            roleManager = getSystemService(RoleManager::class.java)
            return roleManager.isRoleHeld(roleName)
        }
        return false
    }

    private fun requestCallRedirectionPermission(roleName: String) {
        val roleManager: RoleManager?
        if (isGreaterThanOrEqualsAndroidQ()) {
            if (callRedirectionRoleAvailable(roleName)) {
                roleManager = getSystemService(RoleManager::class.java)
                resultLauncher.launch(roleManager.createRequestRoleIntent(roleName))
            } else {
                Toast.makeText(
                    this,
                    "Redirection call with role in not available",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun callRedirectionRoleAvailable(roleName: String): Boolean {
        val roleManager: RoleManager?
        if (isGreaterThanOrEqualsAndroidQ()) {
            roleManager = getSystemService(RoleManager::class.java)
            return roleManager.isRoleAvailable(roleName)
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
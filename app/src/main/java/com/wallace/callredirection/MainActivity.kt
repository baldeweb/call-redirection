package com.wallace.callredirection

import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private var receiver: AttendanceReceiver? = null
    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("LOG", "ResultLauncher: OK")
        } else {
            Log.d("LOG", "ResultLauncher: NOK")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!isRedirection() && isGreaterThanOrEqualsAndroidQ()) {
            requestCallRedirectionPermission(RoleManager.ROLE_CALL_REDIRECTION)
        }

        receiver = AttendanceReceiver()
        registerReceiver(receiver, IntentFilter("ATTENDANCE_REDIRECT_FINISH_CALL").apply {
            addAction("android.intent.action.PHONE_STATE")
            addAction("android.intent.action.NEW_OUTGOING_CALL")
            priority = 1000
        })
        receiver?.onCallFinished = {
            Log.d("LOG", "onCallFinished")
            runOnUiThread {
                startActivity(Intent(this, AttendanceActivity::class.java))
            }
        }
    }

    private fun isGreaterThanOrEqualsAndroidQ() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

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
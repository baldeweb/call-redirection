package com.wallace.callredirection.ui

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.wallace.callredirection.utils.SystemUtils

class RoleCallRedirectionManager {
    companion object {
        fun isRedirection(context: Context): Boolean {
            return if (SystemUtils.isGreaterThanOrEqualsAndroidQ()) {
                isRoleHeldByApp(context, RoleManager.ROLE_CALL_REDIRECTION)
            } else {
                false
            }
        }

        private fun isRoleHeldByApp(context: Context, roleName: String): Boolean {
            val roleManager: RoleManager?
            if (SystemUtils.isGreaterThanOrEqualsAndroidQ()) {
                roleManager = context.getSystemService(RoleManager::class.java)
                return roleManager.isRoleHeld(roleName)
            }
            return false
        }

        fun requestCallRedirectionPermission(
            context: Context,
            resultLauncher: ActivityResultLauncher<Intent>? = null,
            roleName: String
        ) {
            val roleManager: RoleManager?
            if (SystemUtils.isGreaterThanOrEqualsAndroidQ()) {
                if (callRedirectionRoleAvailable(context, roleName)) {
                    roleManager = context.getSystemService(RoleManager::class.java)
                    resultLauncher?.run {
                        this.launch(roleManager.createRequestRoleIntent(roleName))
                    } ?: run {
                        context.startActivity(roleManager.createRequestRoleIntent(roleName))
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Redirection call with role in not available",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        private fun callRedirectionRoleAvailable(context: Context, roleName: String): Boolean {
            val roleManager: RoleManager?
            if (SystemUtils.isGreaterThanOrEqualsAndroidQ()) {
                roleManager = context.getSystemService(RoleManager::class.java)
                return roleManager.isRoleAvailable(roleName)
            }
            return false
        }
    }
}
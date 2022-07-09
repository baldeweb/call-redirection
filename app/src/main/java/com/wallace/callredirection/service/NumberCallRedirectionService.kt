package com.wallace.callredirection.service

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telecom.CallRedirectionService
import android.telecom.PhoneAccountHandle
import androidx.annotation.RequiresApi
import com.wallace.callredirection.utils.Constants.ATTENDANCE_REDIRECT_ACTION
import com.wallace.callredirection.utils.Constants.TRIGGER_NUMBER

@RequiresApi(Build.VERSION_CODES.Q)
class NumberCallRedirectionService : CallRedirectionService() {

    override fun onPlaceCall(
        handle: Uri,
        initialPhoneAccount: PhoneAccountHandle,
        allowInteractiveResponse: Boolean
    ) {
        val phoneNumber = handle.toString()
            .replace("handle:", "")
            .replace("tel:", "")
        catchNumberRedirect(phoneNumber)
    }

    private fun catchNumberRedirect(phoneNumber: String) {
        if (phoneNumber == TRIGGER_NUMBER) {
            Handler(Looper.getMainLooper()).postDelayed({
                cancelCall()
                sendBroadcast(Intent().apply { action = ATTENDANCE_REDIRECT_ACTION })
                startActivity(Intent("android.navigation.attendance").apply {
                    setPackage(packageName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }, 1000)
        } else {
            super.placeCallUnmodified()
        }
    }
}
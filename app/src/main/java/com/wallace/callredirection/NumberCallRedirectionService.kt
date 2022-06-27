package com.wallace.callredirection

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telecom.CallRedirectionService
import android.telecom.PhoneAccountHandle
import android.util.Log
import androidx.annotation.RequiresApi

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
        Log.d("LOG", "[CallRedirectionService] - PhoneNumber: $phoneNumber")
        catchNumberRedirect(phoneNumber)
    }

    private fun catchNumberRedirect(phoneNumber: String) {
        if (phoneNumber == "55555") {
            Handler(Looper.getMainLooper()).postDelayed({
                cancelCall()
                sendBroadcast(Intent().apply { action = "ATTENDANCE_REDIRECT_FINISH_CALL" })
            }, 1000)
        } else {
            super.placeCallUnmodified()
        }
    }
}
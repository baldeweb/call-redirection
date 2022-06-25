package com.wallace.callredirection

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {
    private lateinit var receiver: AttendanceReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        receiver = AttendanceReceiver()
        registerReceiver(receiver, IntentFilter("ATTENDANCE_REDIRECT_FINISH_CALL").apply {
            addAction("android.intent.action.PHONE_STATE")
            addAction("android.intent.action.NEW_OUTGOING_CALL")
            priority = 1000
        })
        receiver.onCallFinished = {
            Log.d("LOG", "onCallFinished")
            startActivity(Intent(this, AttendanceActivity::class.java))
        }
    }
}
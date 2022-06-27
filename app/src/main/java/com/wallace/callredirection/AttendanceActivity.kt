package com.wallace.callredirection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wallace.callredirection.databinding.ActivityAttendanceBinding

class AttendanceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAttendanceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvwTitle.text = "TELA DE\nATENDIMENTO"
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}
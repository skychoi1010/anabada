package com.example.anabada

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}

data class LoginReq (
    val uid: String,
    val upw: String
)

data class LoginRes (
    val success: Boolean,
    val resultCode: String,
    val id: Int,
    val nickname: String
)
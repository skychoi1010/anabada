package com.example.anabada

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}

data class req(val uid:String, val upw:String)

interface login {
    @GET("https://anabada.du.r.appspot.com/api")
    fun
}
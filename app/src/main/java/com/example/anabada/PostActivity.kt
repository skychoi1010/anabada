package com.example.anabada

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.anabada.databinding.ActivityPostBinding

class PostActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}
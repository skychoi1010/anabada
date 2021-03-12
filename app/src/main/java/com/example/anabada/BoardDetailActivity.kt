package com.example.anabada

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.anabada.databinding.ActivityBoardBinding

class BoardDetailActivity: AppCompatActivity() {

    private val boardRecyclerAdapter = BoardRecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView(binding)
    }

    private fun initView(binding: ActivityBoardBinding) {
        binding.rvBoard.adapter = boardRecyclerAdapter
        binding.rvBoard.layoutManager = LinearLayoutManager(this)

        val api = ApiService.create()

    }
}

package com.example.anabada

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.anabada.databinding.ActivityBoardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardActivity: AppCompatActivity() {

    private val boardRecyclerAdapter = BoardRecyclerAdapter()
    val boardsData: BoardsData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView(binding)
    }

    private fun initView(binding: ActivityBoardBinding) {
        binding.rvBoard.adapter = boardRecyclerAdapter
        binding.rvBoard.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this@BoardActivity, LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(this@BoardActivity, R.drawable.divider_gray_ececec)?.let { dividerItemDecoration.setDrawable(it) }
        binding.rvBoard.addItemDecoration(dividerItemDecoration)
        val api = ApiService.create()
        api.reqBoard(1).enqueue(object : Callback<BoardPageRes> {
            override fun onFailure(call: Call<BoardPageRes>, t: Throwable) {
                Toast.makeText(this@BoardActivity, "Failed connection", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<BoardPageRes>, response: Response<BoardsPageRes>) {
                boardsData = response.body()
                val dialog = AlertDialog.Builder(this@BoardActivity)
                dialog.setTitle("success: " + signup?.success.toString())
                dialog.setMessage("result code: " + signup?.resultCode)
                dialog.show()

            }
        })

        boardRecyclerAdapter.setItemClickListener( object : BoardRecyclerAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int) {
                Toast.makeText(this@BoardActivity, "dummy item " + (position+1).toString() + " !!", Toast.LENGTH_SHORT).show()
            }
        })

        binding.floatingActionButton.setOnClickListener{
            val intent = Intent(this@BoardActivity, PostActivity::class.java)
            startActivity(intent)
        }
    }
}

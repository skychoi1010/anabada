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

    var boardPageRes: BoardPageRes? = null
    private var boardsDataList: ArrayList<BoardsData>? = arrayListOf(BoardsData(1, "title", "2020/03/15", 1000, "하늘", "null"),
        BoardsData(2, "title", "2020/03/15", 2000, "하늘", "null"),
        BoardsData(1, "title", "2020/03/15", 1000, "하늘", "null"),
        BoardsData(1, "title", "2020/03/15", 1000, "하늘", "null"),
        BoardsData(1, "title", "2020/03/15", 1000, "하늘", "null"),
        BoardsData(1, "title", "2020/03/15", 1000, "하늘", "null"),
        BoardsData(1, "title", "2020/03/15", 1000, "하늘", "null"),
        BoardsData(1, "title", "2020/03/15", 1000, "하늘", "null"),
        BoardsData(1, "title", "2020/03/15", 1000, "하늘", "null"),
        BoardsData(1, "title", "2020/03/15", 1000, "하늘", "null"),
        BoardsData(1, "title", "2020/03/15", 1000, "하늘", "null"),
        BoardsData(1, "title", "2020/03/15", 1000, "하늘", "null"),
        BoardsData(1, "title", "2020/03/15", 1000, "하늘", "null"),
        BoardsData(1, "title", "2020/03/15", 1000, "하늘", "null"),
        BoardsData(1, "title", "2020/03/15", 1000, "하늘", "null"),
        BoardsData(1, "title", "2020/03/15", 1000, "하늘", "null")) //erase later
    private var boardRecyclerAdapter : BoardRecyclerAdapter? = boardsDataList?.let {
        BoardRecyclerAdapter(
            it
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView(binding)
    }

    private fun initView(binding: ActivityBoardBinding) {
        val api = ApiService.create()
        api.reqBoard(1).enqueue(object : Callback<BoardPageRes> {
            override fun onFailure(call: Call<BoardPageRes>, t: Throwable) {
                Toast.makeText(this@BoardActivity, "board api\nFailed connection", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<BoardPageRes>, response: Response<BoardPageRes>) {
                boardPageRes = response.body()
                Toast.makeText(this@BoardActivity, "board api\nsuccess: " + boardPageRes?.success.toString() +
                        "\nresult code: " + boardPageRes?.resultCode, Toast.LENGTH_SHORT).show()
                //boardsDataList = boardPageRes?.boards
                boardRecyclerAdapter = boardsDataList?.let { BoardRecyclerAdapter(it) }
            }
        })
        binding.rvBoard.adapter = boardRecyclerAdapter
        binding.rvBoard.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this@BoardActivity, LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(this@BoardActivity, R.drawable.divider_gray_ececec)?.let { dividerItemDecoration.setDrawable(it) }
        binding.rvBoard.addItemDecoration(dividerItemDecoration)

        boardRecyclerAdapter?.setItemClickListener( object : BoardRecyclerAdapter.ItemClickListener{
            override fun onClick(view: View, id: Int) {
                val intent = Intent(this@BoardActivity, BoardDetailActivity::class.java)
                intent.putExtra("id", id)
                startActivity(intent)
            }
        })

        binding.floatingActionButton.setOnClickListener{
            val intent = Intent(this@BoardActivity, PostActivity::class.java)
            startActivity(intent)
        }

    }

}

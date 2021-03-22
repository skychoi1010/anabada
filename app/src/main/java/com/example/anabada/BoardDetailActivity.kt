package com.example.anabada

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.anabada.databinding.ActivityBoardBinding
import com.example.anabada.databinding.ActivityBoardDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardDetailActivity: AppCompatActivity() {

    private var boardDetailRes: BoardDetailRes? = null
    val id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBoardDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView(binding)
    }

    private fun initView(binding: ActivityBoardDetailBinding) {

        val api = ApiService.create()

        api.reqBoardDetail(intent.getIntExtra("board id", id)).enqueue(object : Callback<BoardDetailRes> {
            override fun onFailure(call: Call<BoardDetailRes>, t: Throwable) {
                Toast.makeText(this@BoardDetailActivity, "board api\nFailed connection", Toast.LENGTH_SHORT).show()
                //end
            }

            override fun onResponse(call: Call<BoardDetailRes>, response: Response<BoardDetailRes>) {
                boardDetailRes = response.body()
                when {
                    boardDetailRes?.result == null -> {
                        //end
                        Toast.makeText(this@BoardDetailActivity, "페이지를 불러오는 데 실패했습니다.\n" + intent.getIntExtra("board id", id), Toast.LENGTH_SHORT).show()
                    }
                    boardDetailRes?.board?.isEmpty() == true -> {
                        //end
                    }
                    else -> {
                        Toast.makeText(this@BoardDetailActivity, "board api\nsuccess: " + boardDetailRes?.board.toString(), Toast.LENGTH_SHORT).show()

                    }
                }
            }
        })

        /*
        binding.floatingActionButton.setOnClickListener{
            Intent(this@BoardDetailActivity, PostActivity::class.java).apply {
                startActivity(this)
            }
        }
        */


    }
}

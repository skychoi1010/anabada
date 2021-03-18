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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardActivity: AppCompatActivity() {

    private var boardPageRes: BoardPageRes? = null
    private var boardsDataList = ArrayList<BoardsData>()
    //mutableListOf(BoardsData(2,"skyy", "testtitle1", "contents\nnewline\nend\n", 1000, "1", false, "2020/03/16", "2020/03/16", "1", "skyyy"))
    private var boardRecyclerAdapter = BoardRecyclerAdapter(boardsDataList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView(binding)
    }

    private fun initView(binding: ActivityBoardBinding) {
        binding.appbar.tvAppbarLoginLogout.text = "login"
        if (MySharedPreferences.getUserNick(this).isNotEmpty()) {
            let {
                binding.appbar.tvAppbarUserNickname.text = MySharedPreferences.getUserNick(this)
                binding.appbar.tvAppbarLoginLogout.text = "logout"
            }
        }
        val api = ApiService.create()
        var i = true
        var pageNum = 1
        loop@ while (pageNum < 9) {
            api.reqBoard(pageNum).enqueue(object : Callback<BoardPageRes> {
                override fun onFailure(call: Call<BoardPageRes>, t: Throwable) {
                    Toast.makeText(this@BoardActivity, "board api\nFailed connection", Toast.LENGTH_SHORT).show()
                    i = false
                }

                override fun onResponse(call: Call<BoardPageRes>, response: Response<BoardPageRes>) {
                    boardPageRes = response.body()
                    when {
                        boardPageRes?.success == null -> {
                            i = false
                            Toast.makeText(this@BoardActivity, "페이지를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                        boardPageRes?.boards?.isEmpty() == true -> {
                            i = false
                        }
                        else -> {
                            Toast.makeText(this@BoardActivity, "board api\nsuccess: " + boardPageRes?.success.toString() +
                                    "\nresult code: " + boardPageRes?.resultCode + "\nboards: " + boardPageRes?.boards?.get(0)?.title, Toast.LENGTH_SHORT).show()
                            boardPageRes?.boards.also {
                                if (it != null) {
                                    boardsDataList.addAll(it)
                                }
                            }
                            boardsDataList.let { boardRecyclerAdapter.setDataNotify(it) }
                        }
                    }
                }

            })
            pageNum += 1
        }

        binding.rvBoard.adapter = boardRecyclerAdapter
        binding.rvBoard.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this@BoardActivity, LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(this@BoardActivity, R.drawable.divider_gray_ececec)?.let { dividerItemDecoration.setDrawable(it) }
        binding.rvBoard.addItemDecoration(dividerItemDecoration)

        boardRecyclerAdapter.setItemClickListener( object : BoardRecyclerAdapter.ItemClickListener{
            override fun onClick(view: View, id: Int) {
                Intent(this@BoardActivity, BoardDetailActivity::class.java).apply {
                    putExtra("id", id)
                    startActivity(this)
                }
            }
        })

        binding.appbar.tvAppbarLoginLogout.setOnClickListener {
            MySharedPreferences.clearUser(this)
            Intent(this@BoardActivity, MainActivity::class.java).apply {
                startActivity(this)
            }
        }

        binding.floatingActionButton.setOnClickListener{
            Intent(this@BoardActivity, PostActivity::class.java).apply {
                startActivity(this)
            }
        }

    }

}

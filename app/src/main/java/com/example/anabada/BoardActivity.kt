package com.example.anabada

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.anabada.databinding.ActivityBoardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BoardActivity: AppCompatActivity() {

    private var boardPageRes: BoardPageRes? = null
    private var boardsDataList = ArrayList<BoardsData>()
    //mutableListOf(BoardsData(2,"skyy", "testtitle1", "contents\nnewline\nend\n", 1000, "1", false, "2020/03/16", "2020/03/16", "1", "skyyy"))
    private var boardRecyclerAdapter = BoardRecyclerAdapter(boardsDataList)
    var pageNum = 1
    val api = ApiService.create(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView(binding)
        initScrollListener(binding)
    }

    private fun initScrollListener(binding: ActivityBoardBinding) {
        binding.rvBoard.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = binding.rvBoard.layoutManager

                val lastVisibleItem = (layoutManager as LinearLayoutManager)
                    .findLastCompletelyVisibleItemPosition()

                // 마지막으로 보여진 아이템 position 이
                // 전체 아이템 개수보다 5개 모자란 경우, 데이터를 loadMore 한다
                /*if (!binding.rvBoard.canScrollVertically(1)) {

                    pageNum = callBoard(pageNum, api)
                    Toast.makeText(
                        this@BoardActivity,
                        lastVisibleItem.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                */

                val itemTotalCount = layoutManager.itemCount - 1 // 어댑터에 등록된 아이템의 총 개수 -1

                // 스크롤이 끝에 도달했는지 확인
                if (lastVisibleItem == itemTotalCount) {
                    if (pageNum > 0) {
                        pageNum = callBoard(pageNum, api)
                    }
                    Toast.makeText(
                        this@BoardActivity,
                        lastVisibleItem.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }



            }
        })
    }

    private fun initView(binding: ActivityBoardBinding) {
        binding.appbar.tvAppbarLoginLogout.text = "login"
        if (MySharedPreferences.getUserNick(this).isNotEmpty()) {
            let {
                binding.appbar.tvAppbarUserNickname.text = MySharedPreferences.getUserNick(this)
                binding.appbar.tvAppbarLoginLogout.text = "logout"
            }
        }

        if (pageNum > 0) {
            pageNum = callBoard(pageNum, api)
        }

        binding.rvBoard.adapter = boardRecyclerAdapter
        binding.rvBoard.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(
            this@BoardActivity,
            LinearLayoutManager.VERTICAL
        )
        ContextCompat.getDrawable(this@BoardActivity, R.drawable.divider_gray_ececec)?.let { dividerItemDecoration.setDrawable(
            it
        ) }
        binding.rvBoard.addItemDecoration(dividerItemDecoration)

        boardRecyclerAdapter.setItemClickListener(object : BoardRecyclerAdapter.ItemClickListener {
            override fun onClick(view: View, id: Int) {
                Intent(this@BoardActivity, BoardDetailActivity::class.java).apply {
                    putExtra("board id", id)
                    startActivity(this)
                }
            }
        })

        binding.lSwipeRefresh.setOnRefreshListener{
            boardsDataList.clear()

            if (pageNum > 0) {
                pageNum = callBoard(pageNum, api)
            }

            boardsDataList.let { boardRecyclerAdapter.setDataNotify(it) }
            binding.lSwipeRefresh.isRefreshing = false
        }

        binding.appbar.tvAppbarLoginLogout.setOnClickListener {
            api.reqLogout().enqueue(object : Callback<LogoutRes> {
                override fun onFailure(call: Call<LogoutRes>, t: Throwable) {
                    Toast.makeText(
                        this@BoardActivity,
                        "logout api\nFailed connection",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onResponse(call: Call<LogoutRes>, response: Response<LogoutRes>) {
                    MySharedPreferences.clearUser(this@BoardActivity)
                    Intent(this@BoardActivity, MainActivity::class.java).apply {
                        startActivity(this)
                    }
                }
            })
        }

        binding.floatingActionButton.setOnClickListener{
            Intent(this@BoardActivity, PostActivity::class.java).apply {
                startActivity(this)
            }
        }

    }

    private fun callBoard(callNum: Int, api: ApiService): Int {
        var returnNum = callNum
        api.reqBoard(pageNum).enqueue(object : Callback<BoardPageRes> {
            override fun onFailure(call: Call<BoardPageRes>, t: Throwable) {
                Toast.makeText(
                    this@BoardActivity,
                    "board api\nFailed connection",
                    Toast.LENGTH_SHORT
                ).show()
                //end
            }

            override fun onResponse(call: Call<BoardPageRes>, response: Response<BoardPageRes>) {
                boardPageRes = response.body()
                when {
                    boardPageRes?.success == null -> {
                        //end
                        Toast.makeText(
                            this@BoardActivity,
                            "페이지를 불러오는 데 실패했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        returnNum = 0
                    }
                    boardPageRes?.boards?.isEmpty() == true -> {
                        //end
                        Toast.makeText(this@BoardActivity, "end of page", Toast.LENGTH_SHORT).show()
                        //boardsDataList.let { boardRecyclerAdapter.setDataNotify(it) }
                        returnNum = 0
                    }
                    else -> {
                        /*Toast.makeText(this@BoardActivity, "board api\nsuccess: " + boardPageRes?.success.toString() +
                                "\nresult code: " + boardPageRes?.resultCode + "\nboards: " + boardPageRes?.boards?.get(0)?.title, Toast.LENGTH_SHORT).show()*/
                        boardPageRes?.boards.also {
                            if (it != null) {
                                boardsDataList.addAll(it)
                            }
                        }
                        boardsDataList.let { boardRecyclerAdapter.setDataNotify(it) }
                        //callBoard(pageNum + 1, api) //재귀..
                        returnNum++
                    }
                }
            }
        })
        return returnNum
    }

}

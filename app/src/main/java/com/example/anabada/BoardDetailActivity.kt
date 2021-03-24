package com.example.anabada

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.anabada.databinding.ActivityBoardDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardDetailActivity: AppCompatActivity() {

    private var boardDetailRes: BoardDetailRes? = null
    val id = 0
    //ArrayList<CommentData>
    private var commentsPrevDataList = arrayListOf<CommentDetail>(CommentDetail(1, "me","eotrmf 댓글 내용 랄라라랄 \n 랄라랄 hello", "2021/03/23", true), CommentDetail(2, "셔누","eotrmf 댓글 내용 랄라라랄 \n 랄라랄 hello", "2021/03/23", true))
    private var commentsPrevRecyclerAdapter = CommentsPrevRecyclerAdapter(commentsPrevDataList)

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
                    boardDetailRes?.resultCode == null -> {
                        //end
                        Toast.makeText(this@BoardDetailActivity, "페이지를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                    boardDetailRes?.board == null -> {
                        //end
                        Toast.makeText(this@BoardDetailActivity, "게시글이 존재하지 않습니다", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        binding.tvBoardDetailTitle.text = boardDetailRes?.board?.dataValues?.title
                        binding.tvBoardDetailAuthor.text = boardDetailRes?.board?.dataValues?.author
                        binding.tvBoardDetailDate.text = boardDetailRes?.board?.dataValues?.createdAt
                        binding.tvBoardDetailPrice.text = boardDetailRes?.board?.dataValues?.price.toString() + "원"
                        binding.tvBoardDetailContents.text = boardDetailRes?.board?.dataValues?.contents
                    }
                }
            }
        })

        //val pageNum = callComments(1, api)

        binding.rvBoardDetailCommentsPrev.adapter = commentsPrevRecyclerAdapter
        binding.rvBoardDetailCommentsPrev.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this@BoardDetailActivity, LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(this@BoardDetailActivity, R.drawable.divider_gray_ececec)?.let { dividerItemDecoration.setDrawable(it) }
        binding.rvBoardDetailCommentsPrev.addItemDecoration(dividerItemDecoration)

        commentsPrevRecyclerAdapter.setItemClickListener( object : CommentsPrevRecyclerAdapter.ItemClickListener{
            override fun onClick(view: View, id: Int) {
                Intent(this@BoardDetailActivity, BoardDetailActivity::class.java).apply {
                    putExtra("comment id", id)
                    startActivity(this)
                }
            }
        })



        /*
        binding.floatingActionButton.setOnClickListener{
            Intent(this@BoardActivity, PostActivity::class.java).apply {
                startActivity(this)
            }
        }
         */

    }

    /*
    private fun callComments(pageNum: Int, api: ApiService): Int {
        api.reqBoard(pageNum).enqueue(object : Callback<BoardPageRes> {
            override fun onFailure(call: Call<BoardPageRes>, t: Throwable) {
                Toast.makeText(this@BoardActivity, "board api\nFailed connection", Toast.LENGTH_SHORT).show()
                //end
            }

            override fun onResponse(call: Call<BoardPageRes>, response: Response<BoardPageRes>) {
                boardPageRes = response.body()
                when {
                    boardPageRes?.success == null -> {
                        //end
                        Toast.makeText(this@BoardActivity, "페이지를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                    boardPageRes?.boards?.isEmpty() == true -> {
                        //end
                        boardsDataList.let { boardRecyclerAdapter.setDataNotify(it) }
                    }
                    else -> {
                        /*Toast.makeText(this@BoardActivity, "board api\nsuccess: " + boardPageRes?.success.toString() +
                                "\nresult code: " + boardPageRes?.resultCode + "\nboards: " + boardPageRes?.boards?.get(0)?.title, Toast.LENGTH_SHORT).show()*/
                        boardPageRes?.boards.also {
                            if (it != null) {
                                boardsDataList.addAll(it)
                            }
                        }
                        //boardsDataList.let { boardRecyclerAdapter.setDataNotify(it) }
                        callComments(pageNum + 1, api)
                    }
                }
            }
        })
        return pageNum
    }

     */
}

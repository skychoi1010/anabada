package com.example.anabada

import android.R
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.View.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.anabada.databinding.ActivityBoardDetailBinding
import kotlinx.android.synthetic.main.activity_board_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class BoardDetailActivity : AppCompatActivity() {

    private var boardDetailRes: BoardDetailRes? = null
    private var commentRes: CommentRes? = null
    var intentRes: BoardsData? = null

    //ArrayList<CommentData>
    private var commentsPrevDataList = arrayListOf<CommentDetail>()

    //CommentDetail(1, "me","eotrmf 댓글 내용 랄라라랄 \n 랄라랄 hello", "2021/03/23", true), CommentDetail(2, "셔누","eotrmf 댓글 내용 랄라라랄 \n 랄라랄 hello", "2021/03/23", true))
    private var commentsPrevRecyclerAdapter = CommentsPrevRecyclerAdapter(commentsPrevDataList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBoardDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intentRes = intent.getParcelableExtra("board item")

        val title = intentRes?.let { initView(binding, it.id) }
        binding.tvBoardDetailTitle.text = intentRes?.title
        binding.tvBoardDetailAuthor.text = intentRes?.author
        binding.tvBoardDetailDate.text = intentRes?.date
        binding.tvBoardDetailPrice.text = intentRes?.price.toString() + "원"
        /*binding.toolbar.title = "titlee!"
        binding.appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) scrollRange = appBarLayout.totalScrollRange

                if (scrollRange + verticalOffset == 0) {
                    binding.toolbar.title = "Title"
                    isShow = true
                } else if (isShow) {
                    binding.toolbar.title = " " //These quote " " with _ space is intended
                    isShow = false
                }
            }2
        })
         */

        setSupportActionBar(binding.toolbar)
        if (supportActionBar != null) {
            //supportActionBar!!.setDisplayShowTitleEnabled(false)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true) //툴바에 백키(<-) 보이게할거면 이거 사용
            Objects.requireNonNull(supportActionBar)!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = title
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                binding.toolbar.elevation = 1F
                toolbar.setBackgroundColor(Color.GRAY)
            }
            val statusBarHeight = getStatusBarHeight()
            binding.nsvBoardDetail.setPadding(1, 180 + statusBarHeight, 1, 0)
            val params = toolbar.layoutParams
            params.height = statusBarHeight
            toolbar.layoutParams = params
        }

    }

    fun getStatusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
        else Rect().apply { window.decorView.getWindowVisibleDisplayFrame(this) }.top
    }

    private fun initView(binding: ActivityBoardDetailBinding, id: Int): CharSequence? {

        val api = ApiService.create(this)

        api.reqBoardDetail(id).enqueue(object : Callback<BoardDetailRes> {
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
                        binding.tvBoardDetailContents.text = boardDetailRes?.board?.contents
                        Glide.with(this@BoardDetailActivity)
                            .load(boardDetailRes?.board?.detailImg)
                            .apply(RequestOptions().placeholder(R.drawable.ic_dialog_alert))
                            .into(binding.ivBoardDetailImg)
                        binding.tvBoardDetailComment.text = "댓글 " + boardDetailRes?.board?.commentCount.toString() + " > "
                    }
                }
            }
        })

        callComments(1, api, id, binding)
        binding.rvBoardDetailCommentsPrev.adapter = commentsPrevRecyclerAdapter
        binding.rvBoardDetailCommentsPrev.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(
            this@BoardDetailActivity,
            LinearLayoutManager.VERTICAL
        )
        ContextCompat.getDrawable(this@BoardDetailActivity, R.drawable.divider_horizontal_bright)?.let { dividerItemDecoration.setDrawable(it) }
        binding.rvBoardDetailCommentsPrev.addItemDecoration(dividerItemDecoration)

        commentsPrevRecyclerAdapter.setItemClickListener(object : CommentsPrevRecyclerAdapter.ItemClickListener {
            override fun onClick(view: View, id: Int) {
                Intent(this@BoardDetailActivity, BoardDetailActivity::class.java).apply { //TODO comment detail activity
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

        return binding.tvBoardDetailTitle.text
    }


    private fun callComments(pageNum: Int, api: ApiService, id: Int, binding: ActivityBoardDetailBinding) {
        api.reqComment(id, pageNum).enqueue(object : Callback<CommentRes> {
            override fun onFailure(call: Call<CommentRes>, t: Throwable) {
                Toast.makeText(this@BoardDetailActivity, "comment api\nFailed connection", Toast.LENGTH_SHORT).show()
                //end
            }

            override fun onResponse(call: Call<CommentRes>, response: Response<CommentRes>) {
                commentRes = response.body()
                when {
                    commentRes?.success == null -> {
                        //end
                        Toast.makeText(this@BoardDetailActivity, "페이지를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                    commentRes?.comments?.isEmpty() == true -> {
                        //end
                        binding.rvBoardDetailCommentsPrev.visibility = GONE
                        binding.tvBoardDetailNoComment.visibility = VISIBLE
                    }
                    else -> {
                        /*Toast.makeText(this@BoardActivity, "board api\nsuccess: " + boardPageRes?.success.toString() +
                                "\nresult code: " + boardPageRes?.resultCode + "\nboards: " + boardPageRes?.boards?.get(0)?.title, Toast.LENGTH_SHORT).show()*/
                        commentRes?.comments.also {
                            if (it != null) {
                                commentsPrevDataList.addAll(it)
                            }
                        }
                        commentsPrevDataList.let { commentsPrevRecyclerAdapter.setDataNotify(it) }
                        //callComments(pageNum + 1, api, id)
                    }
                }
            }
        })
    }
}

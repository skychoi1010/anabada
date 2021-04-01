package com.example.anabada

import android.R
import android.R.attr
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.widget.NestedScrollView
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBoardDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intentRes = intent.getParcelableExtra("board item")
        intentRes?.let { initView(binding, it.id) }

        binding.tvBoardDetailTitle.text = intentRes?.title
        Log.d("////////intent///", intentRes?.title.toString())
        binding.tvBoardDetailAuthor.text = intentRes?.author
        binding.tvBoardDetailDate.text = intentRes?.date
        binding.tvBoardDetailPrice.text = intentRes?.price.toString() + "원"

        //binding.toolbar.setTitleTextColor(Color.WHITE)
        //binding.toolbar.title = "titlee!"

        var toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        Objects.requireNonNull(supportActionBar)!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar.elevation = 1f // required or it will overlap linear layout


        val scroller = binding.nsvBoardDetail

        scroller.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {
            override fun onScrollChange(
                v: NestedScrollView?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
                if (scrollY > 0) {
                    binding.toolbar.setBackgroundColor(Color.WHITE)
                }
                val headerHeight = binding.ivBoardDetailImg.height - binding.toolbar.height
                var ratio = Math.min(Math.max(scrollY, 0), headerHeight).toFloat() / headerHeight
                val newAlpha = (ratio * 255).toInt()
                Log.d("alphaaaaaa", newAlpha.toString())
                if (newAlpha == 255) {
                    binding.toolbar.title = intentRes?.title
                    binding.toolbar.setTitleTextColor(Color.BLACK)
                } else {
                    binding.toolbar.setTitleTextColor(Color.TRANSPARENT)
                }
                binding.toolbar.background.setAlpha(newAlpha)
                ratio = (newAlpha * 1000).toFloat()
                //binding.toolbar.setTitleTextColor(ratio.toInt())
                /*
                if (attr.scrollY > 0 && attr.scrollY < 700) {
                    val alpha = attr.scrollY / 700f
                    val resultColor = ColorUtils.blendARGB(
                        ContextCompat.getColor(applicationContext, R.color.colorWhite),
                        ContextCompat.getColor(
                            applicationContext, R.color.colorPrimary
                        ),
                        alpha
                    )
                    toolbar.setBackgroundColor(resultColor)
                    Log.i("LOG", "run: " + attr.scrollY + "/" + odlScrollY)
                } else if (attr.scrollY < 50 && odlScrollY > attr.scrollY) {
                    Log.i("LOG", "run: " + attr.scrollY + "/" + odlScrollY)
                    toolbar.setBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.colorWhite
                        )
                    )
                }
                 */
            }
        })
    }

    private fun initView(binding: ActivityBoardDetailBinding, id: Int) {

        val api = ApiService.create(this)

        api.reqBoardDetail(id).enqueue(object : Callback<BoardDetailRes> {
            override fun onFailure(call: Call<BoardDetailRes>, t: Throwable) {
                Toast.makeText(
                    this@BoardDetailActivity,
                    "board api\nFailed connection",
                    Toast.LENGTH_SHORT
                ).show()
                //end
            }

            override fun onResponse(
                call: Call<BoardDetailRes>,
                response: Response<BoardDetailRes>
            ) {
                boardDetailRes = response.body()
                when {
                    boardDetailRes?.resultCode == null -> {
                        //end
                        Toast.makeText(
                            this@BoardDetailActivity,
                            "페이지를 불러오는 데 실패했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    boardDetailRes?.board == null -> {
                        //end
                        Toast.makeText(
                            this@BoardDetailActivity,
                            "게시글이 존재하지 않습니다",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        binding.tvBoardDetailContents.text = boardDetailRes?.board?.contents
                        Glide.with(this@BoardDetailActivity)
                            .load(boardDetailRes?.board?.detailImg)
                            .apply(RequestOptions().placeholder(R.drawable.ic_dialog_alert))
                            .into(binding.ivBoardDetailImg)
                        binding.tvBoardDetailComment.text =
                            "댓글 " + boardDetailRes?.board?.commentCount.toString() + " > "
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
        ContextCompat.getDrawable(this@BoardDetailActivity, R.drawable.divider_horizontal_dim_dark)
                ?.let { dividerItemDecoration.setDrawable(it) }
        binding.rvBoardDetailCommentsPrev.addItemDecoration(dividerItemDecoration)

        commentsPrevRecyclerAdapter.setItemClickListener(object :
            CommentsPrevRecyclerAdapter.ItemClickListener {
            override fun onClick(view: View, id: Int) {
                Intent(
                    this@BoardDetailActivity,
                    BoardDetailActivity::class.java
                ).apply { //TODO comment detail activity
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

    private fun callComments(
        pageNum: Int,
        api: ApiService,
        id: Int,
        binding: ActivityBoardDetailBinding
    ) {
        api.reqComment(id, pageNum).enqueue(object : Callback<CommentRes> {
            override fun onFailure(call: Call<CommentRes>, t: Throwable) {
                Toast.makeText(
                    this@BoardDetailActivity,
                    "comment api\nFailed connection",
                    Toast.LENGTH_SHORT
                ).show()
                //end
            }

            override fun onResponse(call: Call<CommentRes>, response: Response<CommentRes>) {
                commentRes = response.body()
                when {
                    commentRes?.success == null -> {
                        //end
                        Toast.makeText(
                            this@BoardDetailActivity,
                            "페이지를 불러오는 데 실패했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
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

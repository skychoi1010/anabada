package com.example.anabada

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.*
import android.widget.ImageButton
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class BoardDetailActivity : AppCompatActivity() {

    private var boardDetailRes: BoardDetailRes? = null
    private var commentRes: CommentRes? = null
    var intentRes: BoardsData? = null
    private var commentsPrevDataList = arrayListOf<CommentDetail>()

    //CommentDetail(1, "me","eotrmf 댓글 내용 랄라라랄 \n 랄라랄 hello", "2021/03/23", true), CommentDetail(2, "셔누","eotrmf 댓글 내용 랄라라랄 \n 랄라랄 hello", "2021/03/23", true))
    //TODO CommentsDetail과 어댑터 같이 사용중..
    private var commentsPrevRecyclerAdapter = CommentsRecyclerAdapter(commentsPrevDataList, true, this)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //window.statusBarColor = Color.TRANSPARENT
        // window.insetsController?.hide(WindowInsets.Type.statusBars())
        //    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        val binding = ActivityBoardDetailBinding.inflate(layoutInflater)
        //      window.statusBarColor = Color.WHITE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        setContentView(binding.root)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        // window.insetsController?.hide(WindowInsets.Type.statusBars())
        intentRes = intent.getParcelableExtra("board item")
        intentRes?.let { initView(binding, it.id) }

        //binding.tvCommentInput.minHeight = getNavigationBarHeight()
        binding.tvBoardDetailTitle.text = intentRes?.title
        Log.d("////////intent///", intentRes?.title.toString())
        binding.tvBoardDetailAuthor.text = intentRes?.author
        binding.tvBoardDetailDate.text = intentRes?.date
        binding.tvBoardDetailPrice.text = intentRes?.price.toString() + "원"

        //binding.toolbar.setTitleTextColor(Color.WHITE)
        //binding.toolbar.title = "titlee!"

        setSupportActionBar(binding.toolbar)
        Objects.requireNonNull(supportActionBar)!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        binding.toolbar.elevation = 0f // required or it will overlap linear layout

        //toolbar.setPadding(0, getStatusBarHeight(), 0, 0)
        Log.d("status bar height", getStatusBarHeight().toString())
        //TODO status bar + toolbar gradient animation code refactoring
        binding.nsvBoardDetail.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onScrollChange(v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
                if (scrollY < 80) {
                    binding.toolbar.background = ContextCompat.getDrawable(this@BoardDetailActivity, R.drawable.gradient)
                    window.decorView.systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_STABLE)
                } else {
                    binding.toolbar.setBackgroundColor(Color.WHITE)
                    binding.toolbar.setTitleTextColor(Color.TRANSPARENT)
                    binding.toolbar.elevation = 4f // required or it will overlap linear layout
                    initToolbar(binding, scrollY)
                    window.decorView.systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_STABLE)
                    window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }

            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun initToolbar(binding: ActivityBoardDetailBinding, scrollY: Int) {
        val headerHeight = binding.ivBoardDetailImg.height - binding.toolbar.height
        val ratio = Math.min(Math.max(scrollY, 0), headerHeight).toFloat() / headerHeight
        val newAlpha = (ratio * 255).toInt()
        val resultColor = ColorUtils.blendARGB(
                ContextCompat.getColor(applicationContext, R.color.white),
                ContextCompat.getColor(applicationContext, R.color.black), ratio)
        Log.d("alphaaaaaa", newAlpha.toString())
        if (newAlpha == 255) {
            //
            window.decorView.systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.statusBarColor = Color.WHITE
            window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

            window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            binding.toolbar.setTitleTextColor(Color.TRANSPARENT)
        }
        binding.toolbar.background.setAlpha(newAlpha)
        binding.toolbar.setTitleTextColor(resultColor)
        // window.setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.TYPE_STATUS_BAR)
        window.decorView.systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_STABLE)
        //window.statusBarColor = resultColor
        //                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        //                    window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        //                }
        val v: View = binding.toolbar.getChildAt(1)

        //Step 1 : Changing the color of back button (or open drawer button).

        //Step 1 : Changing the color of back button (or open drawer button).
        if (v is ImageButton) {
            //Action Bar back button
            (v as ImageButton).drawable.colorFilter = PorterDuffColorFilter(resultColor, PorterDuff.Mode.SRC_ATOP)
        }
        if (scrollY > headerHeight + binding.toolbar.height) {
            binding.toolbar.title = intentRes?.title
            binding.toolbar.setTitleTextColor(Color.BLACK)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            window.decorView.systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.statusBarColor = Color.WHITE
            window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            //window.insetsController?.show(WindowInsets.Type.statusBars())
            //                    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            //                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            //                            or View.SYSTEM_UI_FLAG_FULLSCREEN)
            //                    //window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            //Rect().apply { window.decorView.getWindowVisibleDisplayFrame(this) }

        } else {
            binding.toolbar.setTitleTextColor(Color.TRANSPARENT)
        }
//        if (scrollY > headerHeight / 2) { //status bar light theme
//            window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            //window.insetsController?.show(WindowInsets.Type.statusBars())
//            //                    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//            //                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//            //                            or View.SYSTEM_UI_FLAG_FULLSCREEN)
//            //                    //window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//            //Rect().apply { window.decorView.getWindowVisibleDisplayFrame(this) }
//
//        }
        Log.d("scrollY", scrollY.toString())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun getStatusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
        else Rect().apply { window.decorView.getWindowVisibleDisplayFrame(this) }.top
    }

    fun getNavigationBarHeight(): Int {
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
        else Rect().apply { window.decorView.getWindowVisibleDisplayFrame(this) }.bottom
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
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
                                .apply(RequestOptions().placeholder(R.drawable.ic_launcher_background))
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
        ContextCompat.getDrawable(this@BoardDetailActivity, R.drawable.divider_gray_ececec)?.let { dividerItemDecoration.setDrawable(it) }
        binding.rvBoardDetailCommentsPrev.addItemDecoration(dividerItemDecoration)

        binding.tvBoardDetailComment.setOnClickListener {
            Intent(this@BoardDetailActivity, CommentsDetailActivity::class.java).apply {
                putExtra("board id", id)
                startActivity(this)
            }
        }

    }

    private fun callComments(callNum: Int, api: ApiService, id: Int, binding: ActivityBoardDetailBinding) {
        api.reqComment(id, callNum).enqueue(object : Callback<CommentRes> {
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

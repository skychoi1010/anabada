package com.example.anabada

import android.R
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.*
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
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
            }
        })
         */

        /*
        toolbar.setBackgroundColor(Color.TRANSPARENT)
        binding.appBarLayout.addOnOffsetChangedListener(object : OnOffsetChangedListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                var verticalOffsetAbs = abs(verticalOffset)
                val view = View(this@BoardDetailActivity)
                var halfScrollRange = (binding.appBarLayout.totalScrollRange.times(0.5f))
                var ratio = verticalOffsetAbs / halfScrollRange
                ratio = 1 - Math.max(0f, Math.min(1f, ratio))
                if (appBarLayout != null) {
                    appBarLayout.elevation = 0F
                    appBarLayout.setBackgroundColor(Color.WHITE)
                    toolbar.setBackgroundColor(Color.WHITE)
                    appBarLayout.background.alpha = (ratio * 255).toInt()
                    binding.toolbar.background.alpha = (ratio * 255).toInt()
                }
            }
        })

         */

        /*
        setSupportActionBar(binding.toolbar)
        if (supportActionBar != null) {
            //supportActionBar!!.setDisplayShowTitleEnabled(false)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true) //툴바에 백키(<-) 보이게할거면 이거 사용=
            supportActionBar!!.title = title
            /*
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                binding.toolbar.elevation = 1F
                toolbar.setBackgroundColor(Color.TRANSPARENT)
            }
            val statusBarHeight = getStatusBarHeight()
            binding.nsvBoardDetail.setPadding(0, 180 + statusBarHeight, 0, 0)
            val params = toolbar.layoutParams
            params.height = 180 + statusBarHeight
            toolbar.layoutParams = params
             */
            getStatusBarHeight()
        }

         */
        // find the toolbar view inside the activity layout
        // find the toolbar view inside the activity layout
        var toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        Objects.requireNonNull(supportActionBar)!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = intentRes?.title.toString()
        toolbar.elevation = 1f // required or it will overlap linear layout

        //toolbar.setBackgroundColor(Color.TRANSPARENT) // required to delete elevation shadow

        // status bar height programmatically , notches...
/*
        val params = binding.toolbar.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null) {
            val valueAnimator = ValueAnimator.ofInt()
            valueAnimator.interpolator = DecelerateInterpolator()
            valueAnimator.addUpdateListener { animation ->
                behavior.topAndBottomOffset = (animation.animatedValue as Int)!!
                appBar.requestLayout()
            }
            valueAnimator.setIntValues(0, -900)
            valueAnimator.duration = 400
            valueAnimator.start()
        }

 */

        var statusBarHeight = getStatusBarHeight()
        //toolbar.marginTop = statusBarHeight
        //val params: ViewGroup.LayoutParams = toolbar.layoutParams
        //params.height = 100 + statusBarHeight
        //toolbar.layoutParams = params


        val scroller = binding.nsvBoardDetail

        scroller.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            /*
            if (scrollY > oldScrollY) {
                // when user scrolls down set toolbar elevation to 4dp
                toolbar.elevation = 4f
                toolbar.setBackgroundColor(Color.WHITE)
            }
            if (scrollY < oldScrollY) {
                // when user scrolls up keep toolbar elevation at 4dp
                toolbar.elevation = 4f
                toolbar.setBackgroundColor(Color.TRANSPARENT)
            }
            if (scrollY == 0) {
                // if user is not scrolling it means
                // that he is at top of page
                toolbar.elevation = 1f // required or it will overlap linear layout
                toolbar.setBackgroundColor(Color.TRANSPARENT) // required to delete elevation shadow
            }

             */
            binding.tvBoardDetailTitle.setOnFocusChangeListener(object : OnFocusChangeListener {
                override fun onFocusChange(v: View?, hasFocus: Boolean) {
                    if (hasFocus) {
                        toolbar.title = intentRes?.title
                    }
                }
            })
            if (scrollY in 1..699) {
                val alpha = scrollY / 700f
                val resultColor = ColorUtils.blendARGB(ContextCompat.getColor(applicationContext, R.color.white), ContextCompat.getColor(applicationContext, R.color.transparent), 1 - alpha)
                toolbar.setBackgroundColor(resultColor)
                //Toast.makeText(this@BoardDetailActivity, "@@@" + intentRes?.title.toString(), Toast.LENGTH_SHORT).show()
                if (toolbar.verticalScrollbarPosition in 1..binding.tvBoardDetailTitle.verticalScrollbarPosition) {
                    Toast.makeText(this@BoardDetailActivity, title, Toast.LENGTH_SHORT).show()
                }
            } else if (scrollY < 50 && oldScrollY > scrollY) {
                toolbar.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.transparent))
            }
        } as NestedScrollView.OnScrollChangeListener)


        /*
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                window.decorView.systemUiVisibility = (
                        SYSTEM_UI_FLAG_LAYOUT_STABLE
                                or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                window.statusBarColor = Color.TRANSPARENT
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                window.decorView.systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                window.statusBarColor = Color.TRANSPARENT
            }
        }

         */


    }

    private fun getStatusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
        else Rect().apply { window.decorView.getWindowVisibleDisplayFrame(this) }.top
    }

    private fun initView(binding: ActivityBoardDetailBinding, id: Int) {

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
        val dividerItemDecoration = DividerItemDecoration(this@BoardDetailActivity, LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(this@BoardDetailActivity, R.drawable.divider_horizontal_dim_dark)
                ?.let { dividerItemDecoration.setDrawable(it) }
        binding.rvBoardDetailCommentsPrev.addItemDecoration(dividerItemDecoration)

        commentsPrevRecyclerAdapter.setItemClickListener(object :
            CommentsPrevRecyclerAdapter.ItemClickListener {
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

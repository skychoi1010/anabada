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
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.PopupMenu
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
import java.text.SimpleDateFormat
import java.util.*


class BoardDetailActivity : AppCompatActivity() {

    private var boardDetailRes: BoardDetailRes? = null
    private var commentRes: CommentRes? = null
    private var editContentRes: EditContentRes? = null
    private var deleteContentRes: DeleteContentRes? = null
    var intentRes: BoardsData? = null
    private var commentsPrevDataList = arrayListOf<CommentDetail>()
    private val api = ApiService.create(this)

    //CommentDetail(1, "me","eotrmf 댓글 내용 랄라라랄 \n 랄라랄 hello", "2021/03/23", true), CommentDetail(2, "셔누","eotrmf 댓글 내용 랄라라랄 \n 랄라랄 hello", "2021/03/23", true))
    //TODO CommentsDetail과 어댑터 같이 사용중..
    private var commentsPrevRecyclerAdapter = CommentsRecyclerAdapter(commentsPrevDataList, true, this)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityBoardDetailBinding.inflate(layoutInflater)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        setContentView(binding.root)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        intentRes = intent.getParcelableExtra("board item")

        //init View !!!
        intentRes?.let { initView(binding, it.id) }

        binding.tvBoardDetailTitle.text = intentRes?.title
        Log.d("////////intent///", intentRes?.title.toString())
        binding.tvBoardDetailAuthor.text = intentRes?.author
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.ss'Z'")
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        binding.tvBoardDetailDate.text = df.format(sdf.parse(intentRes?.date))
        binding.tvBoardDetailPrice.text = intentRes?.price.toString() + "원"

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        binding.toolbar.elevation = 1f

        Log.d("status bar height", getStatusBarHeight().toString())
        //TODO status bar + toolbar gradient animation code refactoring
        binding.nsvBoardDetail.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onScrollChange(v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
                if (scrollY < 80) {
                    binding.toolbar.background = ContextCompat.getDrawable(this@BoardDetailActivity, R.drawable.gradient)
                    val v: View = binding.toolbar.getChildAt(1)
                    if (v is ImageButton) {
                        //Action Bar back button
                        v.drawable.colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
                    }
                    window.decorView.systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_STABLE)
                }
                else if (scrollY > 80){
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
        val headerHeight = binding.ivBoardDetailImg.height - binding.toolbar.height - 80
        val ratio = Math.min(Math.max(scrollY, 0), headerHeight).toFloat() / headerHeight
        val newAlpha = (ratio * 255).toInt()
        val resultColor = ColorUtils.blendARGB(
                ContextCompat.getColor(applicationContext, R.color.white),
                ContextCompat.getColor(applicationContext, R.color.black), ratio)
        Log.d("alphaaaaaa", newAlpha.toString())
        if (newAlpha == 255) {
            window.decorView.systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.statusBarColor = Color.WHITE
            window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            binding.toolbar.setTitleTextColor(Color.TRANSPARENT)
        }
        binding.toolbar.background.alpha = newAlpha
        binding.toolbar.setTitleTextColor(resultColor)
        window.decorView.systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_STABLE)

        val v: View = binding.toolbar.getChildAt(1)
        if (v is ImageButton) {
            //Action Bar back button
            v.drawable.colorFilter = PorterDuffColorFilter(resultColor, PorterDuff.Mode.SRC_ATOP)
        }
        if (scrollY > headerHeight + binding.toolbar.height) {
            binding.toolbar.title = intentRes?.title
            binding.toolbar.setTitleTextColor(Color.BLACK)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            window.decorView.systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.statusBarColor = Color.WHITE
            window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        } else {
            binding.toolbar.setTitleTextColor(Color.TRANSPARENT)
        }
        Log.d("scrollY", scrollY.toString())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getStatusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
        else Rect().apply { window.decorView.getWindowVisibleDisplayFrame(this) }.top
    }

//    fun getNavigationBarHeight(): Int {
//        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
//        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
//        else Rect().apply { window.decorView.getWindowVisibleDisplayFrame(this) }.bottom
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initView(binding: ActivityBoardDetailBinding, id: Int) {

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
                        if (boardDetailRes?.board?.isMine == true) {
                            binding.tvBoardDetailAuthor.text = "내 글"
                            binding.tvBoardDetailOptions.visibility = VISIBLE
                        }
                        binding.tvBoardDetailContents.text = boardDetailRes?.board?.contents
                        Glide.with(this@BoardDetailActivity)
                                .load(boardDetailRes?.board?.detailImg)
                                .apply(RequestOptions().placeholder(R.drawable.ic_launcher_foreground))
                                .into(binding.ivBoardDetailImg)
                        binding.tvBoardDetailComment.text = "댓글 ${boardDetailRes?.board?.commentCount.toString()} > "
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
        ContextCompat.getDrawable(this, R.drawable.divider)?.let { dividerItemDecoration.setDrawable(it) }
        binding.rvBoardDetailCommentsPrev.addItemDecoration(dividerItemDecoration)

        binding.tvBoardDetailComment.setOnClickListener {
            Intent(this@BoardDetailActivity, CommentsDetailActivity::class.java).apply {
                putExtra("board id", id)
                startActivity(this)
            }
        }

        binding.tvBoardDetailOptions.setOnClickListener {
            //creating a popup menu
            val popup = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                PopupMenu(binding.root.context, binding.tvBoardDetailOptions, Gravity.END, 0, R.style.MyPopupMenu)
            } else {
                PopupMenu(binding.root.context, binding.tvBoardDetailOptions)
            }
            //inflating menu from xml resource
            popup.menuInflater.inflate(R.menu.menu_comments, popup.menu)
            //adding click listener
            popup.setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.edit -> {
                        //TODO create edit activity? share post activity?
                        true
                    }
                    R.id.delete -> {
                        Toast.makeText(this@BoardDetailActivity, "delete!", Toast.LENGTH_SHORT).show()
                        deleteContent(id, binding)
                        true
                    }
                    else -> false
                }
            }
            //displaying the popup
            popup.show()
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

    private fun editContent(id: Int, title: String, price: Int, contents: String, imgId: Int) {
        api.reqEditContent(id, title, price, contents, imgId).enqueue(object : Callback<EditContentRes> {
            override fun onFailure(call: Call<EditContentRes>, t: Throwable) {
                Toast.makeText(this@BoardDetailActivity, "content edit api\nFailed connection", Toast.LENGTH_SHORT).show()
                //end
            }

            override fun onResponse(call: Call<EditContentRes>, response: Response<EditContentRes>) {
                editContentRes = response.body()
                when (editContentRes?.resultCode) {
                    null -> {
                        //end
                        Toast.makeText(this@BoardDetailActivity, "게시물 수정에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                    "OK" -> {
                        //end
                        Toast.makeText(this@BoardDetailActivity, "edit content api\n" + editContentRes?.id.toString(), Toast.LENGTH_SHORT).show()
                        Intent(this@BoardDetailActivity, BoardActivity::class.java).apply {
                            startActivity(this)
                        }
                    }
                    else -> {
                        //
                    }
                }
            }
        })
    }

    private fun deleteContent(id: Int, binding: ActivityBoardDetailBinding) {
        api.reqDeleteContent(id).enqueue(object : Callback<DeleteContentRes> {
            override fun onFailure(call: Call<DeleteContentRes>, t: Throwable) {
                Toast.makeText(this@BoardDetailActivity, "delete content api\nFailed connection", Toast.LENGTH_SHORT).show()
                //end
            }

            override fun onResponse(call: Call<DeleteContentRes>, response: Response<DeleteContentRes>) {
                deleteContentRes = response.body()
                when (deleteContentRes?.resultCode) {
                    null -> {
                        //end
                        Toast.makeText(this@BoardDetailActivity, "댓글 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                    "OK" -> {
                        //end
                        Toast.makeText(this@BoardDetailActivity, "delete content api\n" + deleteContentRes?.id.toString(), Toast.LENGTH_SHORT).show()
                        Intent(this@BoardDetailActivity, BoardActivity::class.java).apply {
                            startActivity(this)
                        }
                    }
                    else -> {
                        //
                    }
                }
            }
        })
    }
}

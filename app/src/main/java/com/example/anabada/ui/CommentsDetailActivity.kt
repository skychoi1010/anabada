package com.example.anabada.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.anabada.*
import com.example.anabada.databinding.ActivityCommentsDetailBinding
import com.example.anabada.databinding.ListitemCommentsBinding
import com.example.anabada.network.*
import com.example.anabada.repository.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class CommentsDetailActivity : AppCompatActivity() {

    private var commentRes: CommentRes? = null
    private var postCommentRes: PostCommentRes? = null
    private var editCommentRes: EditCommentRes? = null
    private var deleteCommentRes: DeleteCommentRes? = null
    private var commentsDataList = ArrayList<CommentDetail>()
    private var commentsRecyclerAdapter: CommentsRecyclerAdapter? = null
    var pageNum = 1
    var isPageCallable = true
    private val api = ApiService.create(this)
    var id = 0

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCommentsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        commentsRecyclerAdapter = CommentsRecyclerAdapter(commentsDataList, false, this)
        val temp = 0
        id = intent.getIntExtra("board id", temp)
        Log.d("***id", id.toString())
        initView(binding, id)
        initScrollListener(binding, id)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initView(binding: ActivityCommentsDetailBinding, id: Int) {

        if (isPageCallable) {
            pageNum = 1
            Log.d("//////////init////////", pageNum.toString())
            callComments(pageNum, id, binding)
        }

        setSupportActionBar(binding.toolbar)
        Objects.requireNonNull(supportActionBar)!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        binding.toolbar.elevation = 1f
        supportActionBar!!.title = "댓글 쓰기"
        binding.rvComments.adapter = commentsRecyclerAdapter
        binding.rvComments.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.divider, null))
        binding.rvComments.addItemDecoration(dividerItemDecoration)

        binding.lSwipeRefresh.setOnRefreshListener {
            isPageCallable = true
            pageNum = 1
            commentsDataList.clear()
            commentsDataList.let { commentsRecyclerAdapter?.setDataNotify(it) }
            Log.d("//////////swipe////////", pageNum.toString())
            callComments(pageNum, id, binding)
            binding.lSwipeRefresh.isRefreshing = false
        }

        val inputMethodManager: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(binding.tvCommentInput, 0)

        binding.btnPostComment.setOnClickListener{
            if (MySharedPreferences.getUserId(this) == "no") { // need to login
                Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                Intent(this@CommentsDetailActivity, LoginActivity::class.java).apply {
                    startActivity(this)
                }
            } else {
                val input = binding.tvCommentInput.text.toString()
                postComments(input, id, binding, inputMethodManager)
            }
        }

        commentsRecyclerAdapter?.setItemClickListener(object : CommentsRecyclerAdapter.ItemClickListener {
            override fun onClick(view: View, id: CommentDetail, listitemCommentsBinding: ListitemCommentsBinding) {
                //creating a popup menu
                val popup = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                    PopupMenu(listitemCommentsBinding.root.context, listitemCommentsBinding.tvPrevCommentOptions, Gravity.END, 0, R.style.MyPopupMenu)
                } else {
                    PopupMenu(listitemCommentsBinding.root.context, listitemCommentsBinding.tvPrevCommentOptions)
                }
                //inflating menu from xml resource
                popup.menuInflater.inflate(R.menu.menu_comments, popup.menu)
                //adding click listener
                popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem?): Boolean {
                        return when (item?.itemId) {
                            R.id.edit -> {
                                //                                listitemCommentsBinding.tvPrevComment.visibility = View.INVISIBLE
                                //                                listitemCommentsBinding.etCommentEdit.visibility = View.VISIBLE
                                //                                listitemCommentsBinding.etCommentEdit.setText(listitemCommentsBinding.tvPrevComment.text)
                                //                                listitemCommentsBinding.etCommentEdit.requestFocus()
                                binding.tvCommentInput.setText(listitemCommentsBinding.tvPrevComment.text)
                                inputMethodManager.showSoftInput(binding.tvCommentInput, 0)
                                binding.btnPostComment.visibility = View.INVISIBLE
                                binding.btnEditComment.visibility = View.VISIBLE
                                binding.btnEditComment.setOnClickListener {
                                    if (MySharedPreferences.getUserId(this@CommentsDetailActivity) == "no") { // need to login
                                        Toast.makeText(this@CommentsDetailActivity, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                                        Intent(this@CommentsDetailActivity, LoginActivity::class.java).apply {
                                            startActivity(this)
                                        }
                                    } else {
                                        val input = binding.tvCommentInput.text.toString()
                                        editComments(input, id.id, binding, inputMethodManager)
                                    }
                                }
                                //                                commentsDetailBinding?.tvCommentInput?.requestFocus()
                                //                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                //                                    commentsDetailBinding?.tvCommentInput?.showSoftInputOnFocus
                                //                                }
                                //listitemCommentsBinding.etCommentEdit.text
                                true
                            }
                            R.id.delete -> {
                                deleteComments(id.id, binding)
                                true
                            }
                            else -> false
                        }
                    }
                })
                //displaying the popup
                popup.show()

            }
        })

    }

    private fun initScrollListener(binding: ActivityCommentsDetailBinding, id: Int) {
        binding.rvComments.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var temp: Int = 0
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                temp = 1
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (temp == 1) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = binding.rvComments.layoutManager
                    val lastVisibleItem =
                            (layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                    val itemTotalCount = layoutManager.itemCount - 1 // 어댑터에 등록된 아이템의 총 개수 -1

                    // 스크롤이 끝에 도달했는지 확인
                    if ((lastVisibleItem > 0) && (lastVisibleItem == itemTotalCount)) {
                        if (isPageCallable) {
                            Log.d("/////////scroll///////", pageNum.toString())
                            callComments(pageNum, id, binding)
                        }
                        Toast.makeText(this@CommentsDetailActivity, lastVisibleItem.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

            }
        })
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        Toast.makeText(this@CommentsDetailActivity, "back pressed", Toast.LENGTH_SHORT).show()
//
//        Intent(this@CommentsDetailActivity, BoardDetailActivity::class.java).apply {
//            putExtra("board item", this@CommentsDetailActivity.id)
//            startActivity(this)
//        }
//    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun callComments(callNum: Int, id: Int, binding: ActivityCommentsDetailBinding) {
        Log.d("***comments detail id", id.toString())
        api.reqComment(id, callNum).enqueue(object : Callback<CommentRes> {
            override fun onFailure(call: Call<CommentRes>, t: Throwable) {
                Toast.makeText(this@CommentsDetailActivity, "comment api\nFailed connection", Toast.LENGTH_SHORT).show()
                //end
            }

            override fun onResponse(call: Call<CommentRes>, response: Response<CommentRes>) {
                commentRes = response.body()
                when {
                    commentRes?.success == null -> {
                        //end
                        Toast.makeText(this@CommentsDetailActivity, "페이지를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                    commentRes?.comments?.isEmpty() == true -> {
                        //end
                        if (callNum == 1) {
                            binding.rvComments.visibility = View.GONE
                            binding.tvBoardDetailNoComment.visibility = View.VISIBLE
                        }
                        Toast.makeText(this@CommentsDetailActivity, "end of page", Toast.LENGTH_SHORT).show()
                        //boardsDataList.let { boardRecyclerAdapter.setDataNotify(it) }
                        this@CommentsDetailActivity.isPageCallable = false
                        commentsDataList.let { commentsRecyclerAdapter?.setDataNotify(it) }
                    }
                    else -> {
                        /*Toast.makeText(this@BoardActivity, "board api\nsuccess: " + boardPageRes?.success.toString() +
                                "\nresult code: " + boardPageRes?.resultCode + "\nboards: " + boardPageRes?.boards?.get(0)?.title, Toast.LENGTH_SHORT).show()*/
                        commentRes?.comments.also {
                            if (it != null) {
                                commentsDataList.addAll(it)
                            }
                        }
                        commentsDataList.let { commentsRecyclerAdapter?.setDataNotify(it) }
                        //callComments(pageNum + 1, api, id)
                        this@CommentsDetailActivity.isPageCallable = true
                        this@CommentsDetailActivity.pageNum = callNum + 1
                    }
                }
            }
        })
    }

    private fun postComments(input: String, id: Int, binding: ActivityCommentsDetailBinding, inputMethodManager: InputMethodManager) {
        api.reqPostComment(id, input).enqueue(object : Callback<PostCommentRes> {
            override fun onFailure(call: Call<PostCommentRes>, t: Throwable) {
                Toast.makeText(this@CommentsDetailActivity, "comment post api\nFailed connection", Toast.LENGTH_SHORT).show()
                //end
            }

            override fun onResponse(call: Call<PostCommentRes>, response: Response<PostCommentRes>) {
                postCommentRes = response.body()
                when (postCommentRes?.resultCode) {
                    null -> {
                        //end
                        Toast.makeText(this@CommentsDetailActivity, "comment post api\n result code null", Toast.LENGTH_SHORT).show()
                    }
                    "OK" -> {
                        //end
                        binding.tvCommentInput.text.clear()
                        inputMethodManager.hideSoftInputFromWindow(binding.tvCommentInput.windowToken, 0)
                        commentsDataList.clear()
                        commentsDataList.let { commentsRecyclerAdapter?.setDataNotify(it) }
                        this@CommentsDetailActivity.isPageCallable = true
                        binding.rvComments.visibility = View.VISIBLE
                        binding.tvBoardDetailNoComment.visibility = View.GONE
                        callComments(1, this@CommentsDetailActivity.id, binding)
                        //                        initView(binding, this@CommentsDetailActivity.id)
                        Toast.makeText(this@CommentsDetailActivity, "comment post api\n" + postCommentRes?.id.toString(), Toast.LENGTH_SHORT).show()
                    }
                    "Unauthorized Request" -> {
                        //
                        Toast.makeText(this@CommentsDetailActivity, "Unauthorized Request", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun editComments(input: String, id: Int, binding: ActivityCommentsDetailBinding, inputMethodManager: InputMethodManager) {
        api.reqEditComment(id, input).enqueue(object : Callback<EditCommentRes> {
            override fun onFailure(call: Call<EditCommentRes>, t: Throwable) {
                Toast.makeText(this@CommentsDetailActivity, "comment edit api\nFailed connection", Toast.LENGTH_SHORT).show()
                //end
            }

            override fun onResponse(call: Call<EditCommentRes>, response: Response<EditCommentRes>) {
                editCommentRes = response.body()
                when (editCommentRes?.resultCode) {
                    null -> {
                        //end
                        Toast.makeText(this@CommentsDetailActivity, "댓글 수정에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                    "OK" -> {
                        //end
                        binding.tvCommentInput.text.clear()
                        inputMethodManager.hideSoftInputFromWindow(binding.tvCommentInput.windowToken, 0)
                        binding.btnEditComment.visibility = View.INVISIBLE
                        binding.btnPostComment.visibility = View.VISIBLE
                        commentsDataList.clear()
                        commentsDataList.let { commentsRecyclerAdapter?.setDataNotify(it) }
                        this@CommentsDetailActivity.isPageCallable = true
                        //                        binding.rvComments.visibility = View.VISIBLE
                        //                        binding.tvBoardDetailNoComment.visibility = View.GONE
                        callComments(1, this@CommentsDetailActivity.id, binding)
                        //                        initView(binding, this@CommentsDetailActivity.id)
                        Toast.makeText(this@CommentsDetailActivity, "comment edit api\n" + editCommentRes?.id.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        //
                    }
                }
            }
        })
    }

    private fun deleteComments(id: Int, binding: ActivityCommentsDetailBinding) {
        api.reqDeleteComment(id).enqueue(object : Callback<DeleteCommentRes> {
            override fun onFailure(call: Call<DeleteCommentRes>, t: Throwable) {
                Toast.makeText(this@CommentsDetailActivity, "comment delete api\nFailed connection", Toast.LENGTH_SHORT).show()
                //end
            }

            override fun onResponse(call: Call<DeleteCommentRes>, response: Response<DeleteCommentRes>) {
                deleteCommentRes = response.body()
                when (deleteCommentRes?.resultCode) {
                    null -> {
                        //end
                        Toast.makeText(this@CommentsDetailActivity, "댓글 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                    "OK" -> {
                        //end
                        commentsDataList.clear()
                        commentsDataList.let { commentsRecyclerAdapter?.setDataNotify(it) }
                        //                        binding.rvComments.visibility = View.VISIBLE
                        //                        binding.tvBoardDetailNoComment.visibility = View.GONE
                        this@CommentsDetailActivity.isPageCallable = true
                        callComments(1, this@CommentsDetailActivity.id, binding)
                        //                        initView(binding, this@CommentsDetailActivity.id)
                        Toast.makeText(this@CommentsDetailActivity, "comment delete api\n" + deleteCommentRes?.id.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        //
                    }
                }
            }
        })
    }

}

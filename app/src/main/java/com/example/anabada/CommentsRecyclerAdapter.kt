package com.example.anabada

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.graphics.Color
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.anabada.databinding.ActivityCommentsDetailBinding
import com.example.anabada.databinding.ListitemCommentsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class CommentsRecyclerAdapter(private var commentsPrevDataList: ArrayList<CommentDetail>, private val isPrev: Boolean, private val context: Context): RecyclerView.Adapter<CommentsRecyclerAdapter.CommentsViewHolder>() {

    private val api = ApiService.create(context)

    override fun getItemCount(): Int = commentsPrevDataList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder{
        val binding = ListitemCommentsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        holder.bind(commentsPrevDataList[position])
    }

    fun setDataNotify(commentsPrevDataList: ArrayList<CommentDetail>) {
        this.commentsPrevDataList = commentsPrevDataList
        notifyDataSetChanged()
    }

    inner class CommentsViewHolder(private val listitemCommentsBinding: ListitemCommentsBinding): RecyclerView.ViewHolder(listitemCommentsBinding.root) {
        fun bind(item: CommentDetail) {
            /* //TODO profile image?
            Glide.with(listBinding.root)
                    .load(item.thumbImg)
                    .apply(RequestOptions().placeholder(R.drawable.ic_launcher_background))
                    .into(listBinding.ivBoardThumbnail)
             */
            if (!isPrev) { //comments detail page
                if (item.isMine) {
                    listitemCommentsBinding.tvPrevCommentAuthor.text = "내 댓글" //TODO 버튼 등 처리 동작 구체화 !!
                    listitemCommentsBinding.root.setBackgroundColor(Color.parseColor("#dcffe4"))
                } else {
                    listitemCommentsBinding.tvPrevCommentOptions.visibility = View.INVISIBLE
                    listitemCommentsBinding.tvPrevCommentAuthor.text = item.author
                }
                listitemCommentsBinding.tvPrevCommentOptions.setOnClickListener{
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
                                    listitemCommentsBinding.tvPrevComment.visibility = View.INVISIBLE
                                    listitemCommentsBinding.etCommentEdit.visibility = View.VISIBLE
                                    listitemCommentsBinding.etCommentEdit.setText(listitemCommentsBinding.tvPrevComment.text)
                                    listitemCommentsBinding.etCommentEdit.requestFocus()
//                                    commentsDetailBinding?.tvCommentInput?.setText(listitemCommentsBinding.tvPrevComment.text)
//                                    commentsDetailBinding?.tvCommentInput?.requestFocus()
//                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                                        commentsDetailBinding?.tvCommentInput?.showSoftInputOnFocus
//                                    }
                                    true
                                }
                                R.id.delete -> {
                                    true
                                }
                                else -> false
                            }
                        }
                    })
                    //displaying the popup
                    popup.show()
                }
            } else { //comments prev page
                listitemCommentsBinding.tvPrevCommentOptions.visibility = View.GONE
                if (item.isMine) {
                    listitemCommentsBinding.tvPrevCommentAuthor.text = "내 댓글" //TODO 버튼 등 처리 동작 구체화 !!
                } else {
                    listitemCommentsBinding.tvPrevCommentAuthor.text = item.author
                }
            }

            listitemCommentsBinding.tvPrevCommentDate.text = item.date
            listitemCommentsBinding.tvPrevComment.text = item.contents
        }
    }

//    private fun editComments(input: String, id: Int, binding: ActivityCommentsDetailBinding, inputMethodManager: InputMethodManager) {
//        api.reqReviseComment(id, input).enqueue(object : Callback<PostCommentRes> {
//            override fun onFailure(call: Call<PostCommentRes>, t: Throwable) {
//                Toast.makeText(this@CommentsDetailActivity, "comment post api\nFailed connection", Toast.LENGTH_SHORT).show()
//                //end
//            }
//
//            override fun onResponse(call: Call<PostCommentRes>, response: Response<PostCommentRes>) {
//                postCommentRes = response.body()
//                when (postCommentRes?.resultCode) {
//                    null -> {
//                        //end
//                        Toast.makeText(this@CommentsDetailActivity, "페이지를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
//                    }
//                    "OK" -> {
//                        //end
//                        binding.tvCommentInput.text.clear()
//                        inputMethodManager.hideSoftInputFromWindow(binding.tvCommentInput.windowToken, 0)
//                        commentsDataList.clear()
//                        commentsDataList.let { commentsRecyclerAdapter?.setDataNotify(it) }
//                        binding.rvComments.visibility = View.VISIBLE
//                        binding.tvBoardDetailNoComment.visibility = View.GONE
//                        callComments(1, id, binding)
//                        Toast.makeText(this@CommentsDetailActivity, "comment post api\n" + postCommentRes?.id.toString(), Toast.LENGTH_SHORT).show()
//                    }
//                    else -> {
//                        //
//                    }
//                }
//            }
//        })
//    }
}
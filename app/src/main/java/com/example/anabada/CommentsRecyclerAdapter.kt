package com.example.anabada

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.anabada.DateUtil.convertDateFullToTimestamp
import com.example.anabada.DateUtil.convertTimestampToDateFull
import com.example.anabada.databinding.ActivityCommentsDetailBinding
import com.example.anabada.databinding.ListitemCommentsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class CommentsRecyclerAdapter(private var commentsPrevDataList: ArrayList<CommentDetail>, private val isPrev: Boolean, private val context: Context): RecyclerView.Adapter<CommentsRecyclerAdapter.CommentsViewHolder>() {

    interface ItemClickListener {
        fun onClick(view: View, id: CommentDetail, binding: ListitemCommentsBinding)
    }

    private lateinit var itemClickListener: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

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
                    listitemCommentsBinding.tvPrevCommentOptions.visibility = View.VISIBLE
                    listitemCommentsBinding.tvPrevCommentAuthor.text = "내 댓글" //TODO 버튼 등 처리 동작 구체화 !!
                    listitemCommentsBinding.root.setBackgroundColor(Color.parseColor("#dcffe4"))
                } else {
                    listitemCommentsBinding.tvPrevCommentOptions.visibility = View.INVISIBLE
                    listitemCommentsBinding.tvPrevCommentAuthor.text = item.author
                    listitemCommentsBinding.root.setBackgroundColor(Color.WHITE)
                }
                listitemCommentsBinding.tvPrevCommentOptions.setOnClickListener{
                    itemClickListener.onClick(it, item, listitemCommentsBinding)
                }
            } else { //comments prev page
                listitemCommentsBinding.tvPrevCommentOptions.visibility = View.GONE
                if (item.isMine) {
                    listitemCommentsBinding.tvPrevCommentAuthor.text = "내 댓글" //TODO 버튼 등 처리 동작 구체화 !!
                } else {
                    listitemCommentsBinding.tvPrevCommentAuthor.text = item.author
                }
            }

            listitemCommentsBinding.tvPrevCommentDate.text = item.date.convertDateFullToTimestamp().toString()
            listitemCommentsBinding.tvPrevComment.text = item.contents
        }
    }

}
package com.example.anabada

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.anabada.databinding.ListitemCommentsBinding
import java.text.SimpleDateFormat
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

            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.ss'Z'")
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            listitemCommentsBinding.tvPrevCommentDate.text = df.format(sdf.parse(item.date))
            listitemCommentsBinding.tvPrevComment.text = item.contents
        }
    }

}
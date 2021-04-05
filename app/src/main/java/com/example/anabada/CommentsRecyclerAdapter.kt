package com.example.anabada

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.anabada.databinding.ListitemCommentsBinding
import java.util.*

class CommentsRecyclerAdapter(private var commentsPrevDataList: ArrayList<CommentDetail>): RecyclerView.Adapter<CommentsRecyclerAdapter.CommentsViewHolder>() {

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
            if (item.isMine) {
                listitemCommentsBinding.tvPrevCommentAuthor.text = "!! isMine !!" //TODO 버튼 등 처리 동작 구체화 !!
            } else {

                listitemCommentsBinding.tvPrevCommentAuthor.text = item.author
            }
            listitemCommentsBinding.tvPrevCommentDate.text = item.date
            listitemCommentsBinding.tvPrevComment.text = item.contents
        }
    }

}
package com.example.anabada

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.anabada.databinding.ListitemCommentsPrevBinding
import java.util.ArrayList

class CommentsPrevRecyclerAdapter(private var commentsPrevDataList: ArrayList<CommentDetail>): RecyclerView.Adapter<CommentsPrevRecyclerAdapter.CommentsPrevViewHolder>() {
    interface ItemClickListener {
        fun onClick(view: View, id: Int)
    }

    private lateinit var itemClickListener: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun getItemCount(): Int = commentsPrevDataList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsPrevViewHolder{
        val binding = ListitemCommentsPrevBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentsPrevViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentsPrevViewHolder, position: Int) {
        holder.bind(commentsPrevDataList[position])
    }

    fun setDataNotify(commentsPrevDataList: ArrayList<CommentDetail>) {
        this.commentsPrevDataList = commentsPrevDataList
        notifyDataSetChanged()
    }

    inner class CommentsPrevViewHolder(private val listitemCommentsPrevBinding: ListitemCommentsPrevBinding): RecyclerView.ViewHolder(listitemCommentsPrevBinding.root) {
        fun bind(item: CommentDetail) {
            /* //TODO 이미지 업로드 api 업데이트 이후 다시 복원.
            Glide.with(listBinding.root)
                    .load(item.thumbImg)
                    .apply(RequestOptions().placeholder(R.drawable.ic_launcher_background))
                    .into(listBinding.ivBoardThumbnail)
             */
            //listitemCommentsPrevBinding.tvPrevCommentAuthor.text = item.author
            //listitemCommentsPrevBinding.tvPrevCommentDate.text = item.date
            listitemCommentsPrevBinding.tvPrevComment.text = item.contents
            listitemCommentsPrevBinding.root.setOnClickListener {
                itemClickListener.onClick(it, item.id)
            }
        }
    }

}
package com.example.anabada

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.anabada.databinding.ListitemBoardBinding
import java.text.SimpleDateFormat
import java.util.*


class BoardRecyclerAdapter(private var boardsDataList: ArrayList<BoardsData>): RecyclerView.Adapter<BoardRecyclerAdapter.BoardRecyclerViewHolder>() {

    interface OptionsClickListener {
        fun onOptionsClick(view: View, id: BoardsData, binding: ListitemBoardBinding)
    }

    private lateinit var optionsClickListener: OptionsClickListener

    fun setOptionsClickListener(optionsClickListener: OptionsClickListener) {
        this.optionsClickListener = optionsClickListener
    }

    interface ItemClickListener {
        fun onClick(view: View, id: BoardsData)
    }

    private lateinit var itemClickListener: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun getItemCount(): Int = boardsDataList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardRecyclerViewHolder {
        val binding = ListitemBoardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BoardRecyclerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoardRecyclerViewHolder, position: Int) {
        holder.bind(boardsDataList[position])
    }

    fun setDataNotify(boardsDataList: ArrayList<BoardsData>) {
        this.boardsDataList = boardsDataList
        notifyDataSetChanged()
    }

    inner class BoardRecyclerViewHolder(private val listBinding: ListitemBoardBinding): RecyclerView.ViewHolder(listBinding.root) {
        fun bind(item: BoardsData) {
             //TODO 이미지 업로드 api 업데이트 이후 다시 복원.
            Glide.with(itemView)
                    .load(item.thumbImg)
                    .apply(RequestOptions().placeholder(R.drawable.ic_launcher_foreground))
                    .into(listBinding.ivBoardThumbnail)
            listBinding.tvBoardTitle.text = item.title
            listBinding.tvBoardWriter.text = item.author
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.ss'Z'")
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            listBinding.tvBoardDate.text = df.format(sdf.parse(item.date))
            listBinding.tvBoardPrice.text = item.price.toString()
            listBinding.tvBoardCommentNum.text = item.commentCount.toString()
            if (item.detailImg.isNullOrEmpty()) {
                item.detailImg = "1"
            }
            listBinding.root.setOnClickListener {
                itemClickListener.onClick(it, item)
            }
            listBinding.tvPrevCommentOptions.setOnClickListener{
                optionsClickListener.onOptionsClick(it, item, listBinding)
            }
        }
    }
}
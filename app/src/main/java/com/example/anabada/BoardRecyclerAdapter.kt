package com.example.anabada

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.anabada.databinding.ListitemBoardBinding
import java.util.*


class BoardRecyclerAdapter(private var boardsDataList: ArrayList<BoardsData>): RecyclerView.Adapter<BoardRecyclerAdapter.BoardRecyclerViewHolder>() {

    interface ItemClickListener {
        fun onClick(view: View, id: Int)
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

    inner class BoardRecyclerViewHolder(private val listBinding: ListitemBoardBinding): RecyclerView.ViewHolder(
        listBinding.root
    ) {
        fun bind(item: BoardsData) {
            /* //TODO 이미지 업로드 api 업데이트 이후 다시 복원.
            Glide.with(listBinding.root)
                    .load(item.thumbImg)
                    .apply(RequestOptions().placeholder(R.drawable.ic_launcher_background))
                    .into(listBinding.ivBoardThumbnail)
             */
            listBinding.tvBoardTitle.text = item.title
            listBinding.tvBoardWriter.text = item.author
            listBinding.tvBoardDate.text = item.createdAt
            listBinding.tvBoardPrice.text = item.price.toString()
            listBinding.root.setOnClickListener {
                itemClickListener.onClick(it, item.id)
            }
        }
    }
}
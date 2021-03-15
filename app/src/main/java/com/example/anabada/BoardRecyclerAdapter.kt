package com.example.anabada

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.anabada.databinding.ListitemBoardBinding

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
        val binding = ListitemBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BoardRecyclerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoardRecyclerViewHolder, position: Int) {
        holder.bind(boardsDataList[position])

        holder.itemView.setOnClickListener{
            itemClickListener.onClick(it, position)
        }
    }

    inner class BoardRecyclerViewHolder(private val listBinding: ListitemBoardBinding): RecyclerView.ViewHolder(listBinding.root) {
        fun bind(item: BoardsData) {
            Glide.with(listBinding.root)
                    .load(item.thumbImg)
                    .apply(RequestOptions().placeholder(R.drawable.ic_launcher_background))
                    .into(listBinding.ivBoardThumbnail)
            listBinding.tvBoardTitle.text = item.title
            listBinding.tvBoardWriter.text = item.author
            listBinding.tvBoardDate.text = item.date
            listBinding.tvBoardPrice.text = item.price.toString()
            listBinding.root.setOnClickListener {
                itemClickListener.onClick(it, item.id)
            }
        }
    }

}
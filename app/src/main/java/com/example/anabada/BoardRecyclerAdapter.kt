package com.example.anabada

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.anabada.databinding.ListitemBoardBinding

class BoardRecyclerAdapter(): RecyclerView.Adapter<BoardRecyclerAdapter.BoardRecyclerViewHolder>() {

    var boardRecyclerList = MutableList<Int>(10){ i -> i }

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    private lateinit var itemClickListener: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun getItemCount(): Int = boardRecyclerList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardRecyclerViewHolder {
        val binding = ListitemBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BoardRecyclerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoardRecyclerViewHolder, position: Int) {
        boardRecyclerList[position].let { item ->
            with(holder){
                listBinding.tvBoardTitle.text = "title"
                listBinding.tvBoardWriter.text = "writer"
                listBinding.tvBoardDate.text = "date"
                listBinding.tvBoardPrice.text = "price"
                listBinding.tvBoardCommentNum.text = "1"
                holder.itemView.setOnClickListener{
                    itemClickListener.onClick(it, position)
                }
            }
        }

        holder.itemView.setOnClickListener{
            itemClickListener.onClick(it, position)
        }
    }

    inner class BoardRecyclerViewHolder(val listBinding: ListitemBoardBinding): RecyclerView.ViewHolder(listBinding.root) {}

}
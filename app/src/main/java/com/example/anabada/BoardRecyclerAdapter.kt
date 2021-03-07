package com.example.anabada

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BoardRecyclerAdapter: RecyclerView.Adapter<BoardRecyclerAdapter.BoardRecyclerViewHolder>() {

    var items = MutableList<String>(30){
        index -> mutableListOf<String>(tvGoodsName = "자바칩 프라푸치노 " + (index+1).toString(), tvGoodsPrice = index.toString(), ivGoodsImg = "img_coffee_0" + (index%5).toString())
    }

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    private lateinit var itemClickListener: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardRecyclerViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(BoardActivity, parent, false)
        return BoardRecyclerViewHolder(inflatedView as ViewGroup)
    }

    override fun onBindViewHolder(holder: BoardRecyclerViewHolder, position: Int) {
        items[position].let { item ->
            with(holder){
                tvGoodsNameView.text = item.tvGoodsName
                tvGoodsPriceView.text = item.tvGoodsPrice
                val ivGoodsImgPath = context.resources.getIdentifier(item.ivGoodsImg, "drawable", context.packageName)
                ivGoodsImageView.setImageResource(ivGoodsImgPath)
                holder.itemView.setOnClickListener{
                    itemClickListener.onClick(it, position)
                }
            }
        }

        holder.itemView.setOnClickListener{
            itemClickListener.onClick(it, position)
        }
    }

    inner class BoardRecyclerViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.listitem_shop_item_study, parent, false)) {
        var tvGoodsNameView: TextView = itemView.tv_goods_name_study
        var tvGoodsPriceView: TextView = itemView.tv_goods_price_study
        var ivGoodsImageView: ImageView = itemView.iv_goods_img_study
    }

}
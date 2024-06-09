package com.example.restoapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restoapp.databinding.HistoryCardBinding
import com.example.restoapp.databinding.ProductHistoryCardBinding
import com.example.restoapp.model.DetailProduct
import com.example.restoapp.model.OrderDetail
import com.example.restoapp.util.convertToRupiah
import com.example.restoapp.util.loadImage

class HistoryOrderListAdapter(val historyDetailList: ArrayList<DetailProduct>):RecyclerView.Adapter<HistoryOrderListAdapter.HistoryOrderViewHolder>() {
    class HistoryOrderViewHolder(var binding: ProductHistoryCardBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryOrderViewHolder {
        val binding = ProductHistoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryOrderListAdapter.HistoryOrderViewHolder(binding)
    }

    override fun getItemCount(): Int = historyDetailList.size

    override fun onBindViewHolder(holder: HistoryOrderViewHolder, position: Int) {
        val detailProduct = historyDetailList[position]
        with(holder.binding){
            textProductName.text = detailProduct.name
            textProductDesc.text= detailProduct.description
            textPrice.text = convertToRupiah(detailProduct.price!!)
            textQty.text = detailProduct.quantity.toString()
            imageView.loadImage(detailProduct.image, imageProgress)
        }
    }

    fun updateOrderDetailList(newestOrderDetailList: List<DetailProduct>){
        historyDetailList.clear()
        historyDetailList.addAll(newestOrderDetailList)
        notifyDataSetChanged()
    }
}
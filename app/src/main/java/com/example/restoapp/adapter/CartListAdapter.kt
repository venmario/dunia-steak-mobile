package com.example.restoapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restoapp.databinding.ProductCartCardBinding
import com.example.restoapp.global.GlobalData
import com.example.restoapp.model.OrderDetail
import com.example.restoapp.model.Product
import com.example.restoapp.util.loadImage
import com.example.restoapp.viewmodel.OrderViewModel

class CartListAdapter(private val orderDetailList:ArrayList<OrderDetail>, private val viewModel: OrderViewModel):RecyclerView.Adapter<CartListAdapter.CartViewHolder>() {
    class CartViewHolder(var binding: ProductCartCardBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ProductCartCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return  CartViewHolder(binding)
    }

    override fun getItemCount(): Int = orderDetailList.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val orderDetail = orderDetailList[position]
        with(holder.binding){
            var qty = orderDetail.quantity
            var price = orderDetail.price
            textName.text = orderDetail.product.name
            textDesc.text = orderDetail.product.description
            textCount.text = qty.toString()
            imageViewCartCard.loadImage(orderDetail.product.image, progressBar)
            calculatePrice(price,qty, holder.binding)

            buttonAdd.setOnClickListener {
                qty += 1
                calculatePrice(price,qty,holder.binding)
                udpateOrderDetails(GlobalData.orderDetail, orderDetail.product, qty)
                Log.d("cart list", "global od list : ${GlobalData.orderDetail}")
            }
            buttonRemove.setOnClickListener {
                qty -= 1
                calculatePrice(price,qty,holder.binding)
                udpateOrderDetails(GlobalData.orderDetail, orderDetail.product, qty)
                Log.d("cart list", "global od list : ${GlobalData.orderDetail}")
            }
        }
    }

    private fun udpateOrderDetails(orderDetails: ArrayList<OrderDetail>, product: Product, qty:Int){
        Log.d("cart list ","product id : ${product.id}")
        Log.d("cart list ","qty : $qty")
        val iterator = orderDetails.iterator()
        while (iterator.hasNext()){
            val orderDetail = iterator.next()
            Log.d("cart list", "id product od : ${orderDetail.product.id}")
            if(orderDetail.product.id == product.id){
                if (qty== 0){
                    iterator.remove()
                }else{
                    orderDetail.quantity = qty
                }
                break
            }
        }
        setGrandtotal(orderDetails)
        updateOrderDetailList(orderDetails)
    }

    private fun setGrandtotal(orderDetails: ArrayList<OrderDetail>){
        var grandTotal = 0
        orderDetails.forEach {od->
            grandTotal += (od.quantity*od.price)
        }
        viewModel.setGrandTotal(grandTotal)
    }

    private fun calculatePrice(price:Int, total:Int, binding: ProductCartCardBinding){
        val totalPrice = price*total
        binding.textTotalPrice.text = "Rp${totalPrice}.000"
        binding.textCount.text = total.toString()
    }

    fun updateOrderDetailList(newestOrderDetailList: List<OrderDetail>){
        orderDetailList.clear()
        orderDetailList.addAll(newestOrderDetailList)
        notifyDataSetChanged()
    }
}
package com.example.restoapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.restoapp.databinding.FoodCardBinding
import com.example.restoapp.model.Product
import com.example.restoapp.util.loadImage
import com.example.restoapp.view.MainFragmentDirections

class ProductListAdapter(private val productList:ArrayList<Product>):RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() {
    class ProductViewHolder(var binding: FoodCardBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = FoodCardBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        with(holder.binding){
            textItemName.text = product.name
            textItemDesc.text = product.description
            textItemPrice.text = "${product.price},000"
            imageView.loadImage(product.image, progressImageCard)

            cardViewProductCard.setOnClickListener {
                val action = MainFragmentDirections.actionProductDetail(product.id)
                Navigation.findNavController(it).navigate(action)
            }

            buttonAdd.setOnClickListener {
                val action = MainFragmentDirections.actionProductDetail(product.id)
                Navigation.findNavController(it).navigate(action)
            }
        }
    }

    fun updateProductList(newestProductList: List<Product>){
        productList.clear()
        productList.addAll(newestProductList)
        notifyDataSetChanged()
    }
}
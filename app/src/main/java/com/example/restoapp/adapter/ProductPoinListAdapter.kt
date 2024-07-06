package com.example.restoapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.restoapp.R
import com.example.restoapp.databinding.ProductPoinCardBinding
import com.example.restoapp.model.Product
import com.example.restoapp.util.loadImage
import com.example.restoapp.view.poin.PoinOrderFragmentDirections

class ProductPoinListAdapter(private val productList:ArrayList<Product>, val context:Context):RecyclerView.Adapter<ProductPoinListAdapter.ProductViewHolder>() {
    class ProductViewHolder(var binding: ProductPoinCardBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ProductPoinCardBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int = productList.size
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        with(holder.binding){
            textItemName.text = product.name
            textItemPoin.text = "${product.poin} pts"
            imageView.loadImage(product.image, progressImageCard)

            if (!product.available){
                textItemName.setTextColor(R.color.md_theme_secondary_disable)
                textItemPoin.setTextColor(R.color.md_theme_secondary_disable)
                imageView.strokeColor = ContextCompat.getColorStateList(context, R.color.md_theme_primary_disable)
                buttonAdd.backgroundTintList =  ContextCompat.getColorStateList(context, R.color.md_theme_secondary_disable)
            }

            cardViewProductCard.setOnClickListener {
                if (product.available){
                    val action = PoinOrderFragmentDirections.actionDetailPoint(product.id)
                    Navigation.findNavController(it).navigate(action)
                }
            }

            buttonAdd.setOnClickListener {
                if (product.available){
                    val action = PoinOrderFragmentDirections.actionDetailPoint(product.id)
                    Navigation.findNavController(it).navigate(action)
                }
            }
        }
    }
}
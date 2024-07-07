package com.example.restoapp.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.restoapp.R
import com.example.restoapp.databinding.CategoryFilterBinding
import com.example.restoapp.model.Category
import com.example.restoapp.model.CategoryFilter

class CategoryFilterAdapter(private val categoryFilterList:ArrayList<Category>,private val listener:IFilterListener):RecyclerView.Adapter<CategoryFilterAdapter.CategoryFilterViewHolder>(){
    class CategoryFilterViewHolder(var binding: CategoryFilterBinding):RecyclerView.ViewHolder(binding.root){
        fun onBind(filter: Category) = binding.apply {
            handleSelection(filter)

            textFilter.text = filter.name
        }
        fun handleSelection(filter: Category) = binding.apply{
            if (filter.isSelected){
                textFilter.setTextColor(Color.parseColor("#efcf95"))
            }else{
                textFilter.setTextColor(Color.parseColor("#2e2e2e"))
            }
        }
    }

    interface IFilterListener{
        fun selectCategory(position: Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryFilterViewHolder {
        val binding = CategoryFilterBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CategoryFilterViewHolder(binding)
    }

    override fun getItemCount(): Int = categoryFilterList.size

    override fun onBindViewHolder(holder: CategoryFilterViewHolder, position: Int) {
        val filter = categoryFilterList[position]
        holder.onBind(filter)
        with(holder.binding){
            textFilter.setOnClickListener {
                categoryFilterList.forEach { cat -> cat.isSelected = false }
                filter.isSelected = true

                listener.selectCategory(position)
                notifyDataSetChanged()
            }
        }
    }

    fun updateCategoryFilterList(newestCategoryList: List<Category>){
        categoryFilterList.clear()
        categoryFilterList.addAll(newestCategoryList)
        notifyDataSetChanged()
    }
}
package com.example.restoapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restoapp.databinding.CategoryProductLayoutBinding
import com.example.restoapp.model.Category

class CategoryListAdapter(private val categoryList:ArrayList<Category>):RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder>() {
    class CategoryViewHolder(var binding: CategoryProductLayoutBinding):RecyclerView.ViewHolder(binding.root)
    private val viewPool = RecyclerView.RecycledViewPool()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = CategoryProductLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int = categoryList.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        with(holder.binding){
            textCategoryName.text = category.name
            val productListAdapter = ProductListAdapter(category.product, this.root.context)
            childRecView.layoutManager= LinearLayoutManager(this.root.context)
            childRecView.isNestedScrollingEnabled = false
            childRecView.setRecycledViewPool(viewPool)
            childRecView.adapter = productListAdapter
        }
    }

    fun updatecategoryList(newestCategoryList: List<Category>){
        categoryList.clear()
        categoryList.addAll(newestCategoryList)
        notifyDataSetChanged()
    }

}
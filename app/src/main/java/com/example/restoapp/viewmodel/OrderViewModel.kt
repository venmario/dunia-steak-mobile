package com.example.restoapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.restoapp.model.DetailOrderPoint
import com.example.restoapp.model.Order
import com.example.restoapp.model.Product

class OrderViewModel(application: Application): AndroidViewModel(application) {
    val orderLD = MutableLiveData<Order>()
    val grandTotalLD = MutableLiveData<Int>()

    private val mutableSelectedProductPoint = MutableLiveData<DetailOrderPoint>()
    val selectedProductPoint: LiveData<DetailOrderPoint> get() = mutableSelectedProductPoint
    fun setGrandTotal(grandTotal: Int){
        grandTotalLD.value = grandTotal
    }

    fun setOrder(order:Order){
        orderLD.value = order
    }

    fun selectProductPoint(product: Product, total: Int, note:String?){
        mutableSelectedProductPoint.value = DetailOrderPoint(product, total, note)
    }
}
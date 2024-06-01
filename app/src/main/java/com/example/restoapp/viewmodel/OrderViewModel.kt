package com.example.restoapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.restoapp.model.Order

class OrderViewModel(application: Application): AndroidViewModel(application) {
    val orderLD = MutableLiveData<Order>()
    val grandTotalLD = MutableLiveData<Int>()


    fun setGrandTotal(grandTotal: Int){
        grandTotalLD.value = grandTotal
    }

    fun setOrder(order:Order){
        orderLD.value = order
    }
}
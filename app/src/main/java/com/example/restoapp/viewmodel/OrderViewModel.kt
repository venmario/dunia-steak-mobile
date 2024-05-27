package com.example.restoapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class OrderViewModel(application: Application): AndroidViewModel(application) {
    val grandTotalLD = MutableLiveData<Int>()

    fun setGrandTotal(grandTotal: Int){
        grandTotalLD.value = grandTotal
    }
}
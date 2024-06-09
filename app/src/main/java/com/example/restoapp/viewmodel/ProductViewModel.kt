package com.example.restoapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request.Method
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.restoapp.global.GlobalData
import com.example.restoapp.model.Category
import com.example.restoapp.model.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProductViewModel(application: Application): AndroidViewModel(application) {
    val categoriesLD = MutableLiveData<List<Category>>()
    val productLD = MutableLiveData<Product>()

    val TAG = "volleyTag"
    private var queue: RequestQueue? = null

    val productUrl = "${GlobalData.apiUrl}/product"

    fun getAll(){
        queue = Volley.newRequestQueue(getApplication())
        val url = "${productUrl}/getProductByCategory"
        val stringRequest = StringRequest(
            Method.GET, url, {
                val sType = object : TypeToken<List<Category>>(){}.type
                val result = Gson().fromJson<List<Category>>(it,sType)
                categoriesLD.value = result as ArrayList<Category>?
                Log.d("product", it.toString())
            },{
                Log.d("product error", it.toString())
            }
        )
        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }

    fun getProductById(id:Int){
        queue = Volley.newRequestQueue(getApplication())
        val url = "${productUrl}/getProductById/$id"
        val stringRequest = StringRequest(
            Method.GET, url, {
                val result = Gson().fromJson(it, Product::class.java)
                productLD.value = result
                Log.d("product", it.toString())
            },{
                Log.d("product", it.toString())
            }
        )
        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }

    override fun onCleared() {
        super.onCleared()
        queue?.cancelAll(TAG)
    }
}
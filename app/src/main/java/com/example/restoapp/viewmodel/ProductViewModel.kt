package com.example.restoapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.restoapp.global.GlobalData
import com.example.restoapp.model.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProductViewModel(application: Application): AndroidViewModel(application) {
    val productsLD = MutableLiveData<List<Product>>()
    val productLD = MutableLiveData<Product>()

    val TAG = "volleyTag"
    private var queue: RequestQueue? = null
    val token = "adsad"

    val productUrl = "${GlobalData.apiUrl}/product"

    private fun getAuthorizationHeaders():HashMap<String,String> {
        val headers = HashMap<String,String>()
        headers["Authorization"] = "Bearer $token"
        return  headers
    }
    fun getAll(){
        queue = Volley.newRequestQueue(getApplication())
        val url = "${productUrl}/getProductByCategory/3"
        val stringRequest = object:StringRequest(
            Method.GET, url, {
                val sType = object : TypeToken<List<Product>>(){}.type
                val result = Gson().fromJson<List<Product>>(it,sType)
                productsLD.value = result as ArrayList<Product>?
                Log.d("product", it.toString())
            },{
                Log.d("product error", it.toString())
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                return getAuthorizationHeaders()
            }
        }
        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }

    fun getProductById(id:Int){
        queue = Volley.newRequestQueue(getApplication())
        val url = "${productUrl}/getProductById/$id"
        val stringRequest = object:StringRequest(
            Method.GET, url, {
                val result = Gson().fromJson(it, Product::class.java)
                productLD.value = result
                Log.d("product", it.toString())
            },{
                Log.d("product", it.toString())
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                return getAuthorizationHeaders()
            }
        }
        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }

    override fun onCleared() {
        super.onCleared()
        queue?.cancelAll(TAG)
    }
}
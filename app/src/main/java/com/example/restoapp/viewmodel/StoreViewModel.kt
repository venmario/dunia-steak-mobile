package com.example.restoapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.restoapp.global.GlobalData
import com.example.restoapp.model.ServiceResult
import com.example.restoapp.model.Store
import com.google.gson.Gson
import com.midtrans.sdk.corekit.internal.util.SingleLiveEvent
import org.json.JSONObject

class StoreViewModel(application: Application): AndroidViewModel(application) {
    val store = SingleLiveEvent<ServiceResult<Store>>()

    val storeUrl = "${GlobalData.apiUrl}/store"
    val TAG = "volleyTag"
    private var queue: RequestQueue? = null

    fun getOpenClose(){
        val url = "$storeUrl/getOpenCloseHour"
        queue = Volley.newRequestQueue(getApplication())
        val stringRequest = object: StringRequest(
            Method.GET, url, {
                val result = JSONObject(it)
                Log.d("get store", "get store response : $it")
                val isSuccess = result.getBoolean("isSuccess")
                if(isSuccess){
                    val data = result.getString("data")
                    val resultStore = Gson().fromJson(data, Store::class.java)
                    store.value = ServiceResult(isSuccess,null, resultStore )
                }else{
                    val errorMessage = result.getString("errorMessage")
                    store.value = ServiceResult(isSuccess,errorMessage, null)
                }
            },{
                store.value = ServiceResult(false,it.message, null)
            }
        ){

        }
        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }
}
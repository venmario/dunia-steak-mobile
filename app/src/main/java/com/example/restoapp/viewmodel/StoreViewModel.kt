package com.example.restoapp.viewmodel

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.restoapp.global.GlobalData
import com.example.restoapp.model.ServiceResult
import com.example.restoapp.model.Store
import com.google.gson.Gson
import org.json.JSONObject

class StoreViewModel(application: Application): AndroidViewModel(application) {

    private val mutableStoreTime = MutableLiveData<ServiceResult<Store>>()
    val store: LiveData<ServiceResult<Store>> get() = mutableStoreTime

    val storeUrl = "${GlobalData.apiUrl}/store"
    val TAG = "volleyTag"
    private var queue: RequestQueue? = null

    fun getOpenClose(activity: Activity){
        val url = "$storeUrl/getOpenCloseHour"
        queue = Volley.newRequestQueue(getApplication())
        val stringRequest = object: StringRequest(
            Method.GET, url, {
                val result = JSONObject(it)
                Log.d("get store", "get store response : $it")
                val isSuccess = result.getBoolean("isSuccess")
                if(isSuccess){
                    val data = result.getString("data")
                    val store = Gson().fromJson(data, Store::class.java)
                    mutableStoreTime.value = ServiceResult(isSuccess,null, store )
                }else{
                    val errorMessage = result.getString("errorMessage")
                    mutableStoreTime.value = ServiceResult(isSuccess,errorMessage, null)
                }
            },{
                mutableStoreTime.value = ServiceResult(false,it.message, null)
            }
        ){

        }
        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }
}
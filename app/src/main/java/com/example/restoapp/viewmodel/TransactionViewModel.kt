package com.example.restoapp.viewmodel

import android.app.Activity
import android.app.Application
import android.os.Build
import android.util.Log
import android.view.SurfaceControl.Transaction
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.restoapp.global.GlobalData
import com.example.restoapp.model.OrderDetail
import com.example.restoapp.util.getAuthorizationHeaders
import com.midtrans.sdk.corekit.internal.util.SingleLiveEvent
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TransactionViewModel(application: Application): AndroidViewModel(application) {
    val transactionLD = MutableLiveData<Transaction>()
    val snapToken = SingleLiveEvent<String>()

    val TAG = "volleyTag"
    private var queue: RequestQueue? = null
    val transactionUrl = "${GlobalData.apiUrl}/transaction"

    @RequiresApi(Build.VERSION_CODES.O)
    fun createTransaction(orderDetails: ArrayList<OrderDetail>,activity: Activity){
        queue = Volley.newRequestQueue(getApplication())
        val url = "${transactionUrl}/createTransaction"
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formatted = current.format(formatter)

        val body = JSONObject()
        body.put("estimation", formatted)
        val jsonArray = JSONArray()
        for(orderDetail in orderDetails){
            val jsonObject = JSONObject()
            jsonObject.put("product_id",orderDetail.product.id)
            jsonObject.put("price",orderDetail.price)
            jsonObject.put("poin",orderDetail.poin)
            jsonObject.put("quantity",orderDetail.quantity)
            jsonObject.put("note",orderDetail.note)

            jsonArray.put(jsonObject)
        }
        body.put("order_detail", jsonArray)

        Log.d("body", body.toString())
        val stringRequest = object:JsonObjectRequest(
            Method.POST,url,body,{
                Log.d("create transaction response", it.toString())
                snapToken.value = it.getString("token")
            },{
                Log.d("create transaction response", it.toString())
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                return getAuthorizationHeaders(activity)
            }
        }
        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }

//    fun getParams(): MutableMap<String, String>? {
//        val current = LocalDateTime.now()
//        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//        val formatted = current.format(formatter)
//        val data = HashMap<String,String>()
//        data["estimation"] = formatted
//        val jsonArray = JSONArray()
//        for(orderDetail in orderDetails){
//            val jsonObject = JSONObject()
//            jsonObject.put("product_id",orderDetail.product.id)
//            jsonObject.put("price",orderDetail.price)
//            jsonObject.put("poin",orderDetail.poin)
//            jsonObject.put("quantity",orderDetail.quantity)
//            jsonObject.put("note",orderDetail.note)
//
//            jsonArray.put(jsonObject)
//        }
//        data["order_detail"] = jsonArray
//        Log.d("order detail",data["order_detail"]!!)
//        Log.d("data", data.toString())
//        return data
//    }
    override fun onCleared() {
        super.onCleared()
        queue?.cancelAll(TAG)
    }
}
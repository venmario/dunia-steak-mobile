package com.example.restoapp.viewmodel

import android.app.Activity
import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.restoapp.global.GlobalData
import com.example.restoapp.model.HistoryOrder
import com.example.restoapp.model.HistoryOrderDetail
import com.example.restoapp.model.OrderDetail
import com.example.restoapp.util.getAuthorizationHeaders
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.midtrans.sdk.corekit.internal.util.SingleLiveEvent
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TransactionViewModel(application: Application): AndroidViewModel(application) {
    val historyOrderDetailLD = MutableLiveData<HistoryOrderDetail>()
    val historiesLD = MutableLiveData<ArrayList<HistoryOrder>>()
    val snapToken = SingleLiveEvent<String>()

    val TAG = "volleyTag"
    private var queue: RequestQueue? = null
    private val transactionUrl = "${GlobalData.apiUrl}/transaction"

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

    fun getTransactions(activity: Activity){
        queue = Volley.newRequestQueue(getApplication())
        val url = "${transactionUrl}/getTransactions"
        val stringRequest = object :StringRequest(
            Method.GET,url,{
                Log.d("history", it.toString())
                val sType = object : TypeToken<List<HistoryOrder>>(){}.type
                val result = Gson().fromJson<List<HistoryOrder>>(it,sType)
                historiesLD.value = result as ArrayList<HistoryOrder>
            },{
                Log.d("history err", it.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return getAuthorizationHeaders(activity)
            }
        }
        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }

    fun getTransactionById(activity: Activity, transactionId:String){
        queue = Volley.newRequestQueue(getApplication())
        val url = "${transactionUrl}/getTransactionById/${transactionId}"
        val stringRequest = object :StringRequest(
            Method.GET,url,{
                Log.d("history", it.toString())
                val result = Gson().fromJson(it,HistoryOrderDetail::class.java)
                historyOrderDetailLD.value = result as HistoryOrderDetail
            },{
                Log.d("history err", it.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return getAuthorizationHeaders(activity)
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
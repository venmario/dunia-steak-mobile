package com.example.restoapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request.Method.POST
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.restoapp.global.GlobalData
import com.example.restoapp.model.User
import org.json.JSONObject

class AuthViewModel(application: Application):AndroidViewModel(application) {
    val accToken = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    val registerSuccess = MutableLiveData<Boolean>()

    val authUrl = GlobalData.apiUrl
    val TAG = "volleyTag"
    private var queue: RequestQueue? = null
    fun signIn(email:String,password:String){
        val url = "$authUrl/login"
        queue = Volley.newRequestQueue(getApplication())
        val stringRequest = object: StringRequest(
            Method.POST, url,{
                val result = JSONObject(it)
                Log.d("SIGN IN", "sign in response : $it")
                val code = result.getInt("code")
                if (code == 200){
                    accToken.value = result.getString("token")
                    Log.d("SIGN IN", "acc token : ${accToken.value}")
                }else{
                    errorMessage.value = result.getString("message")
                    Log.d("SIGN IN", "err msg : ${errorMessage.value}")
                }
            },{
                if (it.message!=null){
                    Log.d("sign in error", it.message!!)
                }
            }
        ){
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String,String>()
                params["email"] = email
                params["password"] = password
                return params
            }
        }
        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }

    fun register(user: User){
        val url = "$authUrl/register"
        queue = Volley.newRequestQueue(getApplication())
        val body = JSONObject()
        body.put("username", user.username)
        body.put("firstname",user.firstname)
        body.put("lastname",user.lastname)
        body.put("phonenumber",user.phonenumber)
        body.put("email",user.email)
        body.put("password",user.password)
        val stringRequest = JsonObjectRequest(
            POST,url,body,{
               val success = it.getBoolean("success")
                registerSuccess.value = success
                if(!success)
                    errorMessage.value = "Wrong inputted data"
            },{
                errorMessage.value = it.message
                Log.d("create transaction response", it.toString())
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
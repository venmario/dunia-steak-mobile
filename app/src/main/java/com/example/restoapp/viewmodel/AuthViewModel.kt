package com.example.restoapp.viewmodel

import android.app.Activity
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
import com.example.restoapp.model.LoginResponse
import com.example.restoapp.model.LogoutResponse
import com.example.restoapp.model.RefreshTokenResponse
import com.example.restoapp.model.RegisterResponse
import com.example.restoapp.model.User
import com.example.restoapp.util.getAuthorizationHeaders
import com.google.gson.Gson
import com.midtrans.sdk.corekit.internal.util.SingleLiveEvent
import org.json.JSONObject

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    val loginResponse = SingleLiveEvent<LoginResponse>()
    val registerResponse = SingleLiveEvent<RegisterResponse>()
    val refreshTokenResponse = MutableLiveData<RefreshTokenResponse>()
    val logoutResponse = MutableLiveData<LogoutResponse>()
    val userLD = MutableLiveData<User>()

    val authUrl = GlobalData.apiUrl
    val TAG = "volleyTag"
    private var queue: RequestQueue? = null
    fun signIn(username: String, password: String, oldFcmToken: String, currentFcmToken: String) {
        val url = "$authUrl/login"
        Log.d("url", url)
        queue = Volley.newRequestQueue(getApplication())
        val stringRequest = object : StringRequest(
            POST, url, {
                val result = JSONObject(it)
                Log.d("SIGN IN", "sign in response : $it")
                val code = result.getInt("code")
                if (code == 200) {
                    val accToken = result.getString("token")
                    loginResponse.value = LoginResponse(accToken, username, true, "", null, null)
                    Log.d("SIGN IN", "acc token : $accToken")
                } else {
                    val errMsg = result.getString("message")
                    loginResponse.value = LoginResponse(null, null, false, null, errMsg, null)
                    Log.d("SIGN IN", "err msg : $errMsg")
                }
            }, {
                if (it.message != null) {
                    Log.d("sign in error", it.message!!)
                }
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["username"] = username
                params["password"] = password
                params["oldFcmToken"] = oldFcmToken
                params["currentFcmToken"] = currentFcmToken
                return params
            }
        }
        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }

    fun register(user: User) {
        val url = "$authUrl/register"
        queue = Volley.newRequestQueue(getApplication())
        val body = JSONObject()
        body.put("username", user.username)
        body.put("firstname", user.firstname)
        body.put("lastname", user.lastname)
        body.put("phonenumber", user.phonenumber)
        body.put("email", user.email)
        body.put("password", user.password)
        val stringRequest = JsonObjectRequest(
            POST, url, body, {
                val success = it.getBoolean("success")
                val message = it.getString("message")
                val code = it.getInt("code")
                Log.d("register", it.toString())
                if (success) {
                    registerResponse.value = RegisterResponse(success, message, null,code)
                } else {
                    registerResponse.value = RegisterResponse(success, null, message,code)
                }
            }, {
                registerResponse.value = RegisterResponse(false, null, it.message,767)
                Log.d("register", it.toString())
            }
        )
        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }

    fun logout(activity: Activity, fcmToken: String?) {
        val url = "$authUrl/logout"
        queue = Volley.newRequestQueue(getApplication())
        val stringRequest = object : StringRequest(
            POST, url, {
                Log.d("logout", "response : $it")
                val result = Gson().fromJson(it, LogoutResponse::class.java)
                logoutResponse.value = result
            }, {
                val result = LogoutResponse(false, it.message.toString())
                logoutResponse.value = result
                Log.d("Logout", it.message.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return getAuthorizationHeaders(activity)
            }

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["fcmToken"] = fcmToken.toString()
                return params
            }
        }
        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }

    fun getUser(activity: Activity) {
        val url = "$authUrl/getUser"
        queue = Volley.newRequestQueue(getApplication())
        val stringRequest = object : StringRequest(
            POST, url, {
                val result = JSONObject(it)
                Log.d("get user", it)
                Log.d("get user", "result : $result")
                val user = Gson().fromJson(result.getString("user"), User::class.java)
                userLD.value = user
                Log.d("Profie Page", "user : $it")
                Log.d("Profie PAge", user.toString())
            }, {
                Log.d("Logout", it.message.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return getAuthorizationHeaders(activity)
            }
        }
        stringRequest.tag = TAG
        queue?.add(stringRequest)
    }

    fun refreshToken(activity: Activity) {
        val url = "$authUrl/refresh"
        queue = Volley.newRequestQueue(getApplication())
        val stringRequest = object : StringRequest(
            POST, url, {
                val result = JSONObject(it)
                val code = result.getInt("code")
                val status = result.getString("status")
                if (code == 200) {
                    refreshTokenResponse.value =
                        RefreshTokenResponse(status, result.getString("token"), code)
                } else {
                    refreshTokenResponse.value = RefreshTokenResponse(status, null, code)
                }
            }, {
                refreshTokenResponse.value =
                    RefreshTokenResponse("Something went wrong", it.message, 0)
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
package com.example.restoapp.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.example.restoapp.R
import com.example.restoapp.view.auth.LoginActivity
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

fun ImageView.loadImage(url: String?, progressBar: ProgressBar){
    Picasso.get()
        .load(url)
        .centerCrop()
        .resize(120,120)
        .error(R.drawable.ic_baseline_error_24)
        .into(this, object: Callback {
            override fun onSuccess() {
                progressBar.visibility = View.GONE
            }
            override fun onError(e: Exception?) {
                progressBar.visibility = View.GONE
            }
        })
}

fun getAccToken(activity: Activity):List<String?>{
    val shared = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
    val accToken = shared.getString(LoginActivity.ACCESS_TOKEN, "")
    val username = shared.getString(LoginActivity.USERNAME,"")
    Log.d("util", "acc token : $accToken")
    Log.d("util", "username : $username")
    return listOf( accToken,username )
}

fun setNewAccToken(activity: Activity, newToken:String, username:String){
    val shared = activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = shared.edit()
    editor.putString(LoginActivity.ACCESS_TOKEN,newToken)
    editor.putString(LoginActivity.USERNAME,username)
    editor.apply()
}
fun showToast(message: String,applicationContext:Context) {
    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
}

fun getAuthorizationHeaders(activity: Activity):HashMap<String,String> {
    val (token) = getAccToken(activity)
    val headers = HashMap<String,String>()
    headers["Authorization"] = "Bearer $token"
    return  headers
}
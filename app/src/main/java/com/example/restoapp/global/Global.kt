package com.example.restoapp.global

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.example.restoapp.model.OrderDetail
import com.example.restoapp.view.auth.LoginActivity

object GlobalData {
    const val apiUrl = "https://restoapp.fly.dev/api"
    val orderDetail: ArrayList<OrderDetail> = arrayListOf()


}
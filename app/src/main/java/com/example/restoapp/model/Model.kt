package com.example.restoapp.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class User(
    var id:Int?,
    var username:String?,
    var firstname:String?,
    var lastname:String?,
    var phonenumber:String?,
    var email:String?,
    var password:String?,
    var poin:Int?,
    var role:String
)

data class Category(
    var id:Int,
    var name:String
)

data class Product(
    var id:Int,
    var name:String,
    var description:String,
    var image:String,
    var available:Boolean,
    @SerializedName("category_id")
    var categoryId:Int,
    var price:Int,
    var poin:Int
)

data class Order(
    var id:String,
    var orderAt:Date,
    var grandTotal:Int,
    var grandTotalPoin: Int,
    var userId: Int,
    var orderStatus:String,
    var finishedAt:Date,
    var createdAt:Date,
)

data class OrderDetail(
    var product:Product,
    var price:Int,
    var poin:Int,
    var quantity:Int,
    var note:String?,
)

data class Transcation(
    var transactionId:String?,
    var grossAmount:Double,
    var orderId:String,
    var transactionTime:Date?,
    var settlementTime:Date?,
    var transactionStatus:String?,
    var statusMessage:String?,
    var paymentType:String?,
)

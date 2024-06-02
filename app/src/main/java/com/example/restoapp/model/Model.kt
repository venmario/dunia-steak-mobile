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
    var name:String,
    var product:ArrayList<Product>
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
    var id:String?,
    var orderAt:Date?,
    var grandTotal:Int?,
    var grandTotalPoin: Int?,
    var orderDetails: ArrayList<OrderDetail>,
    var orderStatus:String?,
    var finishedAt:Date?,
    var createdAt:Date?,
)

data class OrderRequest(
    var estimation:String,
    @SerializedName("order_detail")
    var orderDetails: ArrayList<OrderDetailRequest>
)

data class OrderDetailRequest(
    @SerializedName("product_id")
    var productId:Int,
    var price:Int,
    var poin:Int,
    var quantity:Int,
    var note:String?,
)
data class OrderDetail(
    var product:Product,
    var price:Int,
    var poin:Int,
    var quantity:Int,
    var note:String?,
)

data class Transaction(
    var transactionId:String?,
    var grossAmount:Double,
    var order:Order,
    var transactionTime:Date?,
    var settlementTime:Date?,
    var transactionStatus:String?,
    var statusMessage:String?,
    var paymentType:String?,
)

data class TranscationRequest(
    var transactionId:String?,
    var grossAmount:Double,
    var orderId:String,
    var transactionTime:Date?,
    var settlementTime:Date?,
    var transactionStatus:String?,
    var statusMessage:String?,
    var paymentType:String?,
)

data class LoginResponse(
    var accToken:String?,
    var username:String?,
    var success:Boolean,
    var successMessage:String?,
    var errorMessage:String?
)
data class RegisterResponse(
    var success:Boolean,
    var successMessage:String?,
    var errorMessage:String?
)

data class RefreshTokenResponse(
    var status:String,
    var newToken:String?,
    var code:Int
)

data class DetailProduct(
    val name:String,
    val quantity: Int,
    val price: Int?,
    val image: String?,
    val description: String?,
    val note:String?
)
data class HistoryOrder(
    @SerializedName("order_id")
    val orderId:String,
    val status:String,
    @SerializedName("grandtotal")
    val grandTotal: Int,
    @SerializedName("total_item")
    val totalItem: Int,
    @SerializedName("updated_at")
    val updatedAt:String,
    val details: ArrayList<DetailProduct>
)

data class HistoryOrderDetail(
    @SerializedName("order_id")
    val orderId:String,
    val status:String,
    @SerializedName("grandtotal")
    val grandTotal: Int,
    @SerializedName("updated_at_date")
    val date:String,
    @SerializedName("updated_at_time")
    val time:String,
    val details: ArrayList<DetailProduct>
)

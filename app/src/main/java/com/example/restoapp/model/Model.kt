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
    var poin:Int,
    var role:String
)

data class Category(
    var id:Int,
    var name:String,
    var product:ArrayList<Product>,
    var isSelected: Boolean = false
)
data class CategoryFilter(
    var name:String,
    var isSelected:Boolean
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
data class LoginResponse(
    var accToken:String?,
    var username:String?,
    var success:Boolean,
    var successMessage:String?,
    var errorMessage:String?,
    var updatedFcmToken:Boolean?
)

data class ServiceResult<T>(
    var isSuccess: Boolean,
    var errorMessage: String?,
    var data:T?
)
data class LogoutResponse(
    var isSuccess:Boolean,
    var errorMessage: String?
)
data class RegisterResponse(
    var success:Boolean,
    var successMessage:String?,
    var errorMessage:String?,
    var code:Int
)

data class UserResponse(
    var success:Boolean,
    var successMessage:String?,
    var errorMessage:String?,
    var user: User?,
    var code:Int
)

data class UpdateUserResponse(
    var success:Boolean,
    var successMessage:String?,
    var errorMessage:String?,
    var user: User?,
    var code:Int
)
data class UpdateUserPasswordResponse(
    var success:Boolean,
    var successMessage:String?,
    var errorMessage:String?,
    var code:Int
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
    @SerializedName("transaction_id")
    val transactionId: String,
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
    @SerializedName("transaction_id")
    val transactionId: String?,
    val issuer:String?,
    @SerializedName("va_number")
    val vaNumber:String?,
    val bank:String?,
    @SerializedName("payment_type")
    val paymentType: String?,
    @SerializedName("snap_token")
    val snapToken: String,
    @SerializedName("order_id")
    val orderId:String,
    @SerializedName("orderer_name")
    val ordererName:String,
    val status:String,
    val estimation: String,
    @SerializedName("booked_at")
    val bookedAt:String?,
    @SerializedName("grandtotal")
    val grandTotal: Int,
    @SerializedName("updated_at_date")
    val date:String,
    @SerializedName("updated_at_time")
    val time:String,
    @SerializedName("cancel_reason")
    val cancelReason:String?,
    @SerializedName("refund_reason")
    val refundReason:String?,
    val details: ArrayList<DetailProduct>
)

data class TransactionResult(
    @SerializedName("status_code")
    val statusCode:Int,
    @SerializedName("status_message")
    val statusMessage:String,
    @SerializedName("transaction_id")
    val transactionId:String?,
)

data class Notification(
    val transactionId: String,
    val title:String,
    val body:String,
    val date:String,
    var isRead:Boolean
)

data class DetailOrderPoint(
    val product: Product,
    val total: Int,
    val note: String?
)

data class Store(
    var id:Int,
    var open:String,
    var close:String,
    @SerializedName("is_open")
    var isOpen:Boolean
)
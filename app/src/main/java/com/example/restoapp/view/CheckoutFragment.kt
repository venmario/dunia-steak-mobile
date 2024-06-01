package com.example.restoapp.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.auth0.android.jwt.JWT
import com.example.restoapp.adapter.CartListAdapter
import com.example.restoapp.databinding.FragmentCheckoutBinding
import com.example.restoapp.global.GlobalData
import com.example.restoapp.model.OrderDetail
import com.example.restoapp.util.getAccToken
import com.example.restoapp.util.setNewAccToken
import com.example.restoapp.view.auth.LoginActivity
import com.example.restoapp.viewmodel.OrderViewModel
import com.example.restoapp.viewmodel.TransactionViewModel
import com.midtrans.sdk.uikit.api.model.CustomColorTheme
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import com.midtrans.sdk.uikit.internal.util.UiKitConstants
import java.util.Date

class CheckoutFragment : Fragment() {

    private lateinit var binding: FragmentCheckoutBinding
    private lateinit var viewModel: OrderViewModel
    private lateinit var vmTrans: TransactionViewModel
    private lateinit var orderDetailListAdapter:CartListAdapter
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result?.resultCode == AppCompatActivity.RESULT_OK) {
                result.data?.let {
                    val transactionResult = it.getParcelableExtra<TransactionResult>(UiKitConstants.KEY_TRANSACTION_RESULT)
                    Toast.makeText(this.context, "${transactionResult?.transactionId}", Toast.LENGTH_LONG).show()
                }
            }
        }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildUiKit()
        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        vmTrans = ViewModelProvider(this).get(TransactionViewModel::class.java)
        orderDetailListAdapter = CartListAdapter(arrayListOf(), viewModel)

        setGrandtotal(GlobalData.orderDetail)
        binding.recView.layoutManager = LinearLayoutManager(context)
        binding.recView.isNestedScrollingEnabled = false
        binding.recView.adapter = orderDetailListAdapter

        var orderDetails = GlobalData.orderDetail
        Log.d("co frag", orderDetails.toString())
        orderDetailListAdapter.updateOrderDetailList(orderDetails)

        checkOrderDetail(orderDetails)

        binding.buttonContinue.setOnClickListener {
            val (accToken) = getAccToken(requireActivity())
            accToken?.let {
                if (it.isNotEmpty()){
                    val expToken = JWT(it).expiresAt
                    if (expToken != null) {
                        Log.d("exp token", expToken.time.toString())
                        Log.d("now", Date().time.toString())
                        //acctoken expired
                        if( Date().time > expToken.time){
                            Log.d("exp token", "token expired")
                            setNewAccToken(requireActivity(),"","")
                            startActivity(Intent(requireContext(), LoginActivity::class.java))
                        }else{
                            Log.d("exp token", "not expired yet")
                            orderDetails = GlobalData.orderDetail
                            vmTrans.createTransaction(orderDetails,requireActivity())

                            vmTrans.snapToken.observe(viewLifecycleOwner) { token ->
                                Log.d("executed","snaptoken : $token")
                                UiKitApi.getDefaultInstance().startPaymentUiFlow(
                                    requireActivity(),
                                    launcher,
                                    token
                                )
                            }
                        }
                    }
                }else{
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                }
            }

        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.grandTotalLD.observe(viewLifecycleOwner) {
            binding.textTotalPrice.text = "Total Price : Rp${it.toString()}.000"
            if (it == 0) {
                binding.scrollView.visibility = View.GONE
                binding.buttonParent.visibility = View.GONE
                binding.textNoOrder.visibility = View.VISIBLE
            } else {
                binding.scrollView.visibility = View.VISIBLE
                binding.buttonParent.visibility = View.VISIBLE
                binding.textNoOrder.visibility = View.GONE
            }

        }
    }

    private fun setGrandtotal(orderDetails: ArrayList<OrderDetail>){
        var grandTotal = 0
        orderDetails.forEach {od->
            grandTotal += (od.quantity*od.price)
        }
        viewModel.setGrandTotal(grandTotal)
    }

    private fun buildUiKit() {
        UiKitApi.Builder()
            .withContext(requireContext())
            .withMerchantUrl("https://restoapp.fly.dev/api/")
            .withMerchantClientKey("SB-Mid-client-3bwg6AeHieg8hIXf")
            .enableLog(true)
            .withColorTheme(CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
            .build()
        uiKitCustomSetting()
    }

    private fun uiKitCustomSetting() {
        val uIKitCustomSetting = UiKitApi.getDefaultInstance().uiKitSetting
        uIKitCustomSetting.saveCardChecked = true
    }

    private fun checkOrderDetail(orderDetails: ArrayList<OrderDetail>){
        if(orderDetails.isEmpty()){
            binding.scrollView.visibility = View.GONE
            binding.buttonParent.visibility = View.GONE
            binding.textNoOrder.visibility = View.VISIBLE
        }else{
            binding.scrollView.visibility = View.VISIBLE
            binding.buttonParent.visibility = View.VISIBLE
            binding.textNoOrder.visibility = View.GONE
        }
    }
}
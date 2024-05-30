package com.example.restoapp.view

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
import com.example.restoapp.adapter.CartListAdapter
import com.example.restoapp.databinding.FragmentCheckoutBinding
import com.example.restoapp.global.GlobalData
import com.example.restoapp.model.OrderDetail
import com.example.restoapp.viewmodel.OrderViewModel
import com.example.restoapp.viewmodel.TransactionViewModel
import com.midtrans.sdk.uikit.api.model.CustomColorTheme
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import com.midtrans.sdk.uikit.internal.util.UiKitConstants

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
        binding.recView.adapter = orderDetailListAdapter

        var orderDetails = GlobalData.orderDetail
        orderDetailListAdapter.updateOrderDetailList(orderDetails)

        binding.buttonContinue.setOnClickListener {
            orderDetails = GlobalData.orderDetail
            vmTrans.createTransaction(orderDetails,requireActivity())

            vmTrans.snapToken.observe(viewLifecycleOwner) { token ->
                Log.d("executed","snaptoken : $token")
                UiKitApi.getDefaultInstance().startPaymentUiFlow(
                    requireActivity(),
                    launcher,
                    token.toString()
                )
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.grandTotalLD.observe(viewLifecycleOwner, {
            binding.textTotalPrice.text = "Total Price : Rp${it.toString()}.000"
        })
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
}
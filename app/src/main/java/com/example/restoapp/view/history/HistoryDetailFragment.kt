package com.example.restoapp.view.history

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restoapp.R
import com.example.restoapp.adapter.HistoryOrderListAdapter
import com.example.restoapp.databinding.FragmentHistoryDetailBinding
import com.example.restoapp.util.convertToRupiah
import com.example.restoapp.viewmodel.OrderViewModel
import com.example.restoapp.viewmodel.TransactionViewModel
import com.midtrans.sdk.uikit.api.model.CustomColorTheme
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import com.midtrans.sdk.uikit.internal.util.UiKitConstants

class HistoryDetailFragment : Fragment() {
    private lateinit var binding: FragmentHistoryDetailBinding
    private lateinit var viewmodel: TransactionViewModel
    private val historyOrderDetailListAdapter = HistoryOrderListAdapter (arrayListOf())
    private val vmOrder: OrderViewModel by activityViewModels()

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("result code",result.resultCode.toString())
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                result.data?.let {
                    val transactionResult = it.getParcelableExtra<TransactionResult>(UiKitConstants.KEY_TRANSACTION_RESULT)
                    Log.d("transaction result", "transaction id : ${transactionResult?.transactionId}")
                    if (transactionResult != null) {
                        when(transactionResult.status){
                            UiKitConstants.STATUS_CANCELED -> {
                                Toast.makeText(context,"CANCELED", Toast.LENGTH_LONG)
                            }
                            UiKitConstants.STATUS_SETTLEMENT -> {
                                val transactionId = transactionResult.transactionId
                                viewmodel.getTransactionById(requireActivity(), transactionId!!)
                                binding.layoutPendingPayment.visibility = View.GONE
                            }
                            else -> {
                                val transactionId = transactionResult.transactionId
                                viewmodel.getTransactionById(requireActivity(), transactionId!!)
                            }
                        }
                    }
                }
            }
        }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildUiKit()
        binding.shimmerLayout.startShimmer()

        binding.recView.layoutManager = LinearLayoutManager(context)
        binding.recView.adapter = historyOrderDetailListAdapter
        binding.vaContainer.visibility = View.GONE
        binding.layoutPendingPayment.visibility = View.GONE
        binding.containerCancelReason.visibility = View.GONE
        binding.containerRefundReason.visibility = View.GONE
        binding.layoutPendingPaymentBankTF.visibility = View.GONE
        viewmodel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        val transactionId = HistoryDetailFragmentArgs.fromBundle(requireArguments()).transactionId
        viewmodel.getTransactionById(requireActivity(), transactionId)
        observeVM()
    }

    private fun observeVM() {
        viewmodel.historyOrderDetailLD.observe(viewLifecycleOwner){
            if (it!=null){
                with(binding){
                    shimmerLayout.stopShimmer()
                    shimmerLayout.visibility = View.GONE
                    dataLayout.visibility = View.VISIBLE
                    layoutPendingPayment.visibility = View.GONE
                    layoutPendingPaymentBankTF.visibility = View.GONE
                    containerCancelReason.visibility = View.GONE
                    containerRefundReason.visibility = View.GONE
                    when(it.paymentType){
                        "shopeepay" -> {
                            imagePaymentPlatform.setImageResource(com.midtrans.sdk.uikit.R.drawable.ic_em_shopeepay_40)
                            Log.d("status", it.status)
                            if (it.status == "waiting payment"){
                                layoutPendingPayment.visibility = View.VISIBLE
                            }
                        }
                        "gopay" -> {
                            imagePaymentPlatform.setImageResource(R.drawable.gopay)
                            if (it.status == "waiting payment"){
                                layoutPendingPayment.visibility = View.VISIBLE
                            }
                        }
                        "bank_transfer"->{
                            val resource = when(it.bank){
                                "bca" -> com.midtrans.sdk.uikit.R.drawable.ic_bank_bca_40
                                else -> com.midtrans.sdk.uikit.R.drawable.ic_bank_bri_40
                            }
                            imagePaymentPlatform.setImageResource(resource)
                            if (it.status == "waiting payment") {
                                vaContainer.visibility = View.VISIBLE
                                vaNumber.text = it.vaNumber
                                layoutPendingPaymentBankTF.visibility = View.VISIBLE
                                buttonCancelBankTF.setOnClickListener {btn->
                                    vmOrder.selectCancelOrder(it.orderId)
                                    val action = HistoryDetailFragmentDirections.actionCanclerOrder()
                                    Navigation.findNavController(btn).navigate(action)
                                }
                            }
                        }
                    }
                    if (it.status == "Processing"){
                        textReady.text = "Ready at ${it.estimation}"
                        textReady.visibility = View.VISIBLE
                    }else if(it.status == "cancel"){
                        txtCancelReason.text = it.cancelReason
                        containerCancelReason.visibility = View.VISIBLE
                    }else if(it.status == "refund"){
                        txtCancelReason.text = it.refundReason
                        containerRefundReason.visibility = View.VISIBLE
                    }
                    textStatus.text = it.status.uppercase()
                    textOrderId.text = "#${it.orderId}"
                    textDate.text = it.date.uppercase()
                    textTime.text = it.time.uppercase()
                    if (it.bookedAt != null){
                        layoutBooking.visibility = View.VISIBLE
                        textBookingTime.text = it.bookedAt
                    }
                    txtOrdererName.text = it.ordererName
                    txtPaymentMethod.text = when(it.paymentType){
                        "bank_transfer"-> {
                            when(it.bank){
                                "bca" -> "BCA Virtual Account"
                                else -> "BRI Virtual Account"
                            }
                        }
                        "shopeepay" -> "ShopeePay"
                        "Redeem Point"-> "Redeem Point"
                        else -> "GoPay"
                    }
                    txtTotal.text = convertToRupiah(it.grandTotal)
                    buttonCancel.setOnClickListener {btn->
                        vmOrder.selectCancelOrder(it.orderId)
                        val action = HistoryDetailFragmentDirections.actionCanclerOrder()
                        Navigation.findNavController(btn).navigate(action)
                    }
                    buttonPay.setOnClickListener {_ ->
                        UiKitApi.getDefaultInstance().startPaymentUiFlow(
                            activity = requireActivity(),
                            launcher = launcher,
                            snapToken = it.snapToken
                        )
                    }
                }
                historyOrderDetailListAdapter.updateOrderDetailList(it.details)
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun buildUiKit() {
        UiKitApi.Builder()
            .withContext(requireContext())
            .withMerchantUrl("https://restoapp.fly.dev/api/")
            .withMerchantClientKey("SB-Mid-client-3bwg6AeHieg8hIXf")
            .enableLog(true)
            .withColorTheme(CustomColorTheme(getString(R.color.md_theme_secondary), getString(R.color.md_theme_secondary),getString(R.color.md_theme_secondary)))
            .build()
        uiKitCustomSetting()
    }

    private fun uiKitCustomSetting() {
        val uIKitCustomSetting = UiKitApi.getDefaultInstance().uiKitSetting
        uIKitCustomSetting.saveCardChecked = true
    }
}
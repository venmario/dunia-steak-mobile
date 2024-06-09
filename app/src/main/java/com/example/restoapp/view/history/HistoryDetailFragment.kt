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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restoapp.R
import com.example.restoapp.adapter.HistoryOrderListAdapter
import com.example.restoapp.databinding.FragmentHistoryDetailBinding
import com.example.restoapp.util.convertToRupiah
import com.example.restoapp.viewmodel.TransactionViewModel
import com.midtrans.sdk.uikit.api.model.CustomColorTheme
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import com.midtrans.sdk.uikit.internal.util.UiKitConstants

class HistoryDetailFragment : Fragment() {
    private lateinit var binding: FragmentHistoryDetailBinding
    private lateinit var viewmodel: TransactionViewModel
    private val historyOrderDetailListAdapter = HistoryOrderListAdapter (arrayListOf())

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
                                binding.buttonPay.visibility = View.GONE
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

        binding.recView.layoutManager = LinearLayoutManager(context)
        binding.recView.adapter = historyOrderDetailListAdapter
        binding.vaContainer.visibility = View.GONE
        binding.buttonPay.visibility = View.GONE
        viewmodel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        val transactionId = HistoryDetailFragmentArgs.fromBundle(requireArguments()).transactionId
        viewmodel.getTransactionById(requireActivity(), transactionId)
        observeVM()
    }

    private fun observeVM() {
        viewmodel.historyOrderDetailLD.observe(viewLifecycleOwner){
            if (it!=null){
                with(binding){
                    buttonPay.visibility = View.GONE
                    when(it.paymentType){
                        "shopeepay" -> {
                            imagePaymentPlatform.setImageResource(com.midtrans.sdk.uikit.R.drawable.ic_em_shopeepay_40)
                            Log.d("status", it.status)
                            if (it.status == "waiting payment"){
                                buttonPay.visibility = View.VISIBLE
                                buttonPay.text = "Pay with ShopeePay"
                            }
                        }
                        "gopay" -> {
                            imagePaymentPlatform.setImageResource(R.drawable.gopay)
                            if (it.status == "waiting payment"){
                                buttonPay.visibility = View.VISIBLE
                                buttonPay.text = "Pay with Gopay"
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
                            }
                        }
                    }
                    textStatus.text = it.status.uppercase()
                    textOrderId.text = "ORDER ID #${it.orderId}"
                    textDate.text = it.date.uppercase()
                    textTime.text = it.time.uppercase()
                    txtOrdererName.text = it.ordererName
                    txtPaymentMethod.text = when(it.paymentType){
                        "bank_transfer"-> {
                            when(it.bank){
                                "bca" -> "BCA Virtual Account"
                                else -> "BRI Virtual Account"
                            }
                        }
                        "shopeepay" -> "ShopeePay"
                        else -> "GoPay"
                    }
                    txtTotal.text = convertToRupiah(it.grandTotal)
                    buttonPay.setOnClickListener {btn ->
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
package com.example.restoapp.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.auth0.android.jwt.JWT
import com.example.restoapp.R
import com.example.restoapp.adapter.CartListAdapter
import com.example.restoapp.bean.DsDateValidator
import com.example.restoapp.databinding.FragmentCheckoutBinding
import com.example.restoapp.global.GlobalData
import com.example.restoapp.model.OrderDetail
import com.example.restoapp.util.convertToRupiah
import com.example.restoapp.util.getAccToken
import com.example.restoapp.util.setNewAccToken
import com.example.restoapp.view.auth.LoginActivity
import com.example.restoapp.viewmodel.OrderViewModel
import com.example.restoapp.viewmodel.StoreViewModel
import com.example.restoapp.viewmodel.TransactionViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.midtrans.sdk.uikit.api.model.CustomColorTheme
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import com.midtrans.sdk.uikit.internal.util.UiKitConstants
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class CheckoutFragment : Fragment() {

    private lateinit var binding: FragmentCheckoutBinding
    private lateinit var viewModel: OrderViewModel
    private lateinit var vmTrans: TransactionViewModel
    private lateinit var vmStore: StoreViewModel
    private lateinit var orderDetailListAdapter:CartListAdapter
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("result code",result.resultCode.toString())
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                result.data?.let {
                    val transactionResult = it.getParcelableExtra<TransactionResult>(UiKitConstants.KEY_TRANSACTION_RESULT)
                    Log.d("transaction result", "transaction id : ${transactionResult?.transactionId}")
                    if (transactionResult != null) {
                        Log.d("transactionResult", "not null")
                        when(transactionResult.status){
                            UiKitConstants.STATUS_CANCELED -> {
                                Log.d("transactionResult.status","canceled")
                                binding.buttonContinue.text = "CHECK OUT"
                                enableButton()
                            }
                            else -> {
                                Log.d("transactionResult stats",transactionResult.status)
                                GlobalData.orderDetail.clear()
                                val transactionId = transactionResult.transactionId
                                val action = CheckoutFragmentDirections.actionAfterTransaction(transactionId!!)
                                Navigation.findNavController(requireView()).navigate(action)
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
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildUiKit()
        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        vmTrans = ViewModelProvider(this).get(TransactionViewModel::class.java)
        vmStore = ViewModelProvider(this).get(StoreViewModel::class.java)
        orderDetailListAdapter = CartListAdapter(arrayListOf(), viewModel)

        setGrandtotal(GlobalData.orderDetail)
        binding.recView.layoutManager = LinearLayoutManager(context)
        binding.recView.isNestedScrollingEnabled = false
        binding.recView.adapter = orderDetailListAdapter

        var orderDetails = GlobalData.orderDetail
        Log.d("co frag", orderDetails.toString())
        orderDetailListAdapter.updateOrderDetailList(orderDetails)

        checkOrderDetail(orderDetails)
        var time:String? = null
        var date:String? = null
        var isScheduledOrder = false
        notScheduledOrder()

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
            .setTheme(R.style.BaseTheme_TimePicker)
            .setTitleText("Order for")
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute

            val today = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))

            val selectedBookingDateTime = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))


            val splittedDate = date!!.split("-")
//            val selectedBookingDateTime = Date(splittedDate[0].toInt(), splittedDate[1].toInt(), splittedDate[2].toInt(), hour, minute)
//            Log.d("date", "${splittedDate[0].toInt()}, ${splittedDate[1].toInt()}, ${splittedDate[2].toInt()}, $hour, $minute")
            selectedBookingDateTime.set(splittedDate[0].toInt(), splittedDate[1].toInt()-1, splittedDate[2].toInt(), hour, minute)
            Log.d("today", today.timeInMillis.toString())
            Log.d("selectedBookingDatetime", selectedBookingDateTime.time.toString())
            if (today.timeInMillis < selectedBookingDateTime.timeInMillis){
                time = String.format("%02d:%02d", hour, minute)

                isScheduledOrder = true
                scheduledOrder()
                binding.textOrderAt.text = "$date $time"
            }else{
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Alert!")
                    .setMessage("Cannot book at passed hour")
                    .show()
                binding.chipOrderNow.isChecked = true
                notScheduledOrder()
                time = null
            }
        }
        timePicker.addOnDismissListener {
            if (!isScheduledOrder){
                binding.chipOrderNow.isChecked = true
                notScheduledOrder()
                time = null
            }
        }

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))

        val currentDate = calendar.timeInMillis
        Log.d("time", "date : ${calendar.get(Calendar.DAY_OF_MONTH)}, hour : ${calendar.get(Calendar.HOUR)}, minute : ${calendar.get(Calendar.MINUTE)}")

        val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val remainingDays = lastDayOfMonth - currentDayOfMonth
        val endDate: Long
        if (remainingDays < 7){
            calendar[Calendar.MONTH] = Calendar.getInstance().get(Calendar.MONTH) + 1
            endDate = calendar.timeInMillis
        }else{
            endDate = currentDate
        }
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setStart(currentDate)
                .setEnd(endDate)
                .setValidator(DsDateValidator())

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(currentDate)
            .setCalendarConstraints(constraintsBuilder.build())
            .setTheme(R.style.ThemeOverlay_App_DatePicker)
            .build()

        datePicker.addOnPositiveButtonClickListener {
            if (datePicker.selection != null){
                val selectedDate = Calendar.getInstance()
                selectedDate.timeInMillis = datePicker.selection!!
                val selectedDay = selectedDate.get(Calendar.DAY_OF_MONTH)
                val selectedMonth = selectedDate.get(Calendar.MONTH) + 1
                val selectedYear = selectedDate.get(Calendar.YEAR)
                date = String.format("%d-%02d-%02d",selectedYear, selectedMonth, selectedDay)
                timePicker.show(parentFragmentManager, "timePicker")
                Log.d("datepicker",datePicker.selection.toString())
            }
        }
        datePicker.addOnDismissListener {
            Log.d("datepicker","dismiss")
            if (date == null){
                binding.chipOrderNow.isChecked = true
                notScheduledOrder()
            }
        }

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

                            binding.buttonContinue.text = "Loading..."
                            disableButton()

                            vmStore.getOpenClose()
                            vmStore.store.observe(viewLifecycleOwner){store->
                                if (store.isSuccess){
                                    val open = store.data!!.open
                                    val close = store.data!!.close

                                    val localTimeOpen = LocalTime.parse(open, DateTimeFormatter.ofPattern("HH:mm:ss"))
                                    val localTimeClose = LocalTime.parse(close, DateTimeFormatter.ofPattern("HH:mm:ss"))

                                    val formatter = DateTimeFormatter.ofPattern("HH:mm")

                                    val formattedOpen = localTimeOpen.format(formatter)
                                    val formattedClose = localTimeClose.format(formatter)
                                    if (store.data!!.isOpen){
                                        if (isScheduledOrder){
                                            val localTimeSchedule = LocalTime.parse("$time:00", DateTimeFormatter.ofPattern("HH:mm:ss"))
                                            if (!localTimeSchedule.isAfter(localTimeOpen)){
                                                Log.d("localTime","after")
                                            }
                                            if (localTimeSchedule.isBefore(localTimeClose)){
                                                Log.d("localTime","before")
                                            }

                                            if (!localTimeSchedule.isAfter(localTimeOpen) || localTimeSchedule.isAfter(localTimeClose)) {
                                                MaterialAlertDialogBuilder(requireContext())
                                                    .setTitle("Warning!")
                                                    .setMessage("Cannot book at closed hour. Operating hours : ${formattedOpen} - ${formattedClose}")
                                                    .show()
                                                enableButton()
                                                binding.buttonContinue.text = "CHECK OUT"
                                                return@observe
                                            }
                                        }
                                        Log.d("localtime","test")
                                        val dateTime = "$date $time"
                                        vmTrans.createTransaction(orderDetails,isScheduledOrder,dateTime,requireActivity())
                                    }else{
                                        //store closed
                                        MaterialAlertDialogBuilder(requireContext())
                                            .setTitle("Warning!")
                                            .setMessage("Store Closed. Operating hours : ${formattedOpen} - ${formattedClose}")
                                            .show()
                                        enableButton()
                                        binding.buttonContinue.text = "CHECK OUT"
                                    }
                                }
                            }
                        }
                    }
                }else{
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                }
            }

        }

        binding.chipOrderNow.setOnClickListener {
            isScheduledOrder = false
            date = null
            time = null
            notScheduledOrder()
        }

        binding.chipScheduleOrder.setOnClickListener {
            Log.d("chip schedule order","clicked")
            date = null
            datePicker.show(parentFragmentManager, "tag")
        }

        observeViewModel()
    }
    private fun enableButton() {
        with(binding){
            buttonContinue.isEnabled = true
            buttonContinue.setTextColor(resources.getColorStateList(R.color.md_theme_secondary))
            buttonContinue.backgroundTintList = resources.getColorStateList(R.color.md_theme_primary)

        }
    }
    private fun disableButton() {
        binding.buttonContinue.isEnabled = false
        binding.buttonContinue.setTextColor(resources.getColorStateList(R.color.md_theme_secondary_disable))
        binding.buttonContinue.backgroundTintList = resources.getColorStateList(R.color.md_theme_primary_disable)
    }

    private fun observeViewModel() {
        viewModel.grandTotalLD.observe(viewLifecycleOwner) {
            binding.textTotalPrice.text = convertToRupiah(it)
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
        vmTrans.snapToken.observe(viewLifecycleOwner) { token ->
            Log.d("executed","snaptoken : $token")
            UiKitApi.getDefaultInstance().startPaymentUiFlow(
                requireActivity(),
                launcher,
                token
            )
        }


    }

    private fun setGrandtotal(orderDetails: ArrayList<OrderDetail>){
        var grandTotal = 0
        orderDetails.forEach {od->
            grandTotal += (od.quantity*od.price)
        }
        viewModel.setGrandTotal(grandTotal)
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

    private fun scheduledOrder(){
        binding.layoutSchedule.visibility = View.VISIBLE
    }

    private fun notScheduledOrder(){
        binding.layoutSchedule.visibility = View.GONE
    }
}
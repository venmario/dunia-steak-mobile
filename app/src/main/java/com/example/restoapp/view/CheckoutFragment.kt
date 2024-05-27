package com.example.restoapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restoapp.adapter.CartListAdapter
import com.example.restoapp.databinding.FragmentCheckoutBinding
import com.example.restoapp.global.GlobalData
import com.example.restoapp.model.OrderDetail
import com.example.restoapp.viewmodel.OrderViewModel

class CheckoutFragment : Fragment() {

    private lateinit var binding: FragmentCheckoutBinding
    private lateinit var viewModel: OrderViewModel
    private lateinit var orderDetailListAdapter:CartListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        orderDetailListAdapter = CartListAdapter(arrayListOf(), viewModel)

        setGrandtotal(GlobalData.orderDetail)
        binding.recView.layoutManager = LinearLayoutManager(context)
        binding.recView.adapter = orderDetailListAdapter

        val orderDetails = GlobalData.orderDetail
        orderDetailListAdapter.updateOrderDetailList(orderDetails)

        binding.buttonContinue.setOnClickListener {

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
}
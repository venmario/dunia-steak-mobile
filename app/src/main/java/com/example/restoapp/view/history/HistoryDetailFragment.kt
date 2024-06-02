package com.example.restoapp.view.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restoapp.adapter.HistoryOrderListAdapter
import com.example.restoapp.databinding.FragmentHistoryDetailBinding
import com.example.restoapp.viewmodel.TransactionViewModel

class HistoryDetailFragment : Fragment() {
    private lateinit var binding: FragmentHistoryDetailBinding
    private lateinit var viewmodel: TransactionViewModel
    private val historyOrderDetailListAdapter = HistoryOrderListAdapter (arrayListOf())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recView.layoutManager = LinearLayoutManager(context)
        binding.recView.adapter = historyOrderDetailListAdapter

        viewmodel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        val orderId = HistoryDetailFragmentArgs.fromBundle(requireArguments()).orderId
        viewmodel.getOrderById(requireActivity(), orderId)
        observeVM()
    }

    private fun observeVM() {
        viewmodel.historyOrderDetailLD.observe(viewLifecycleOwner){
            if (it!=null){
                with(binding){
                    textStatus.text = it.status.uppercase()
                    textOrderId.text = "ORDER ID #${it.orderId}"
                    textDate.text = it.date.uppercase()
                    textTime.text = it.time.uppercase()
                    txtPrice.text = "Rp ${it.grandTotal}"
                    txtTotal.text = "Rp ${it.grandTotal}"

                }
                historyOrderDetailListAdapter.updateOrderDetailList(it.details)
            }
        }
    }
}
package com.example.restoapp.view.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restoapp.adapter.HistoryListAdapter
import com.example.restoapp.databinding.FragmentHistoryListBinding
import com.example.restoapp.viewmodel.TransactionViewModel

class HistoryListFragment : Fragment() {
    private lateinit var binding: FragmentHistoryListBinding
    private lateinit var viewmodel: TransactionViewModel
    private val historyListAdapter = HistoryListAdapter(arrayListOf())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        binding.historyRecView.layoutManager = LinearLayoutManager(context)
        binding.historyRecView.adapter = historyListAdapter
        viewmodel.getTransactions(requireActivity())
        observeVM()
    }

    private fun observeVM() {
        viewmodel.historiesLD.observe(viewLifecycleOwner){
            historyListAdapter.updateHistoryList(it)
        }
    }
}
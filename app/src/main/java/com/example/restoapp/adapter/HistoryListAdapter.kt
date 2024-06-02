package com.example.restoapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.restoapp.databinding.HistoryCardBinding
import com.example.restoapp.model.HistoryOrder
import com.example.restoapp.view.history.HistoryListFragmentDirections

class HistoryListAdapter(private val historyList:ArrayList<HistoryOrder>):RecyclerView.Adapter<HistoryListAdapter.HistoryListViewHolder>() {
    class HistoryListViewHolder(var binding: HistoryCardBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryListViewHolder {
        val binding = HistoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryListViewHolder(binding)
    }

    override fun getItemCount(): Int = historyList.size

    override fun onBindViewHolder(holder: HistoryListViewHolder, position: Int) {
        val history = historyList[position]
        with(holder.binding){
            textStatus.text = history.status.uppercase()
            textOrderTime.text = history.updatedAt
            var detailsInString:ArrayList<String> = arrayListOf()
            for (detail in history.details){
                detailsInString.add("${detail.quantity} ${detail.name}")
            }
            var details = detailsInString.joinToString(separator = ", ")
            textDetail.text = details
            textTotal.text = "${history.totalItem} item - Rp ${history.grandTotal}"
            cardTransactionParent.setOnClickListener {
                val orderId = history.orderId
                val action = HistoryListFragmentDirections.actionToHistoryDetail(orderId)
                Navigation.findNavController(it).navigate(action)
            }
        }
    }

    fun updateHistoryList(newestHistoryList: List<HistoryOrder>){
        historyList.clear()
        historyList.addAll(newestHistoryList)
        notifyDataSetChanged()
    }
}
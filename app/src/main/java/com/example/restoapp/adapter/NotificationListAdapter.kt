package com.example.restoapp.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.restoapp.databinding.NotifCardBinding
import com.example.restoapp.model.Notification
import com.example.restoapp.view.ListNotificationFragmentDirections

class NotificationListAdapter(private val notificationList:ArrayList<Notification>):RecyclerView.Adapter<NotificationListAdapter.NotificationListViewHolder>() {
    class  NotificationListViewHolder(var binding: NotifCardBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationListViewHolder {
        val binding = NotifCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationListViewHolder(binding)
    }

    override fun getItemCount(): Int = notificationList.size

    override fun onBindViewHolder(holder: NotificationListViewHolder, position: Int) {
        val notification = notificationList[position]
        with (holder.binding){
            if (!notification.isRead){
                cardNotification.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#EFCF95")))
            }
            textTitle.text = notification.title
            textBody.text = notification.body
            textDateNotification.text = notification.date
            cardNotification.setOnClickListener {
                notification.isRead = true
                val action = ListNotificationFragmentDirections.actionNotifToHistoryDetail(notification.transactionId)
                Navigation.findNavController(it).navigate(action)
            }
        }
    }


    fun updateNotificationList(newestNotificationList: List<Notification>){
        notificationList.clear()
        notificationList.addAll(newestNotificationList)
        notifyDataSetChanged()
    }
}
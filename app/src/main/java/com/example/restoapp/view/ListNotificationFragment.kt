package com.example.restoapp.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restoapp.adapter.NotificationListAdapter
import com.example.restoapp.databinding.FragmentListNotificationBinding
import com.example.restoapp.viewmodel.NotificationViewModel

class ListNotificationFragment : Fragment() {

    private lateinit var binding: FragmentListNotificationBinding
    private lateinit var viewModel: NotificationViewModel
    private val notificationListAdapter = NotificationListAdapter(arrayListOf())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recViewNotification.layoutManager = LinearLayoutManager(context)
        binding.recViewNotification.adapter = notificationListAdapter
        viewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)
        viewModel.getAll()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.notificationsLD.observe(viewLifecycleOwner){
            Log.d("notificationsLD", it.toString())
            notificationListAdapter.updateNotificationList(it)
        }
    }
}
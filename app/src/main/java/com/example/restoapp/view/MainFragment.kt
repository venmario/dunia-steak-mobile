package com.example.restoapp.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.auth0.android.jwt.JWT
import com.example.restoapp.adapter.CategoryListAdapter
import com.example.restoapp.databinding.FragmentMainBinding
import com.example.restoapp.util.getAccToken
import com.example.restoapp.util.setNewAccToken
import com.example.restoapp.view.auth.LoginActivity
import com.example.restoapp.viewmodel.ProductViewModel
import com.example.restoapp.viewmodel.StoreViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
class MainFragment : Fragment() {
    private lateinit var binding:FragmentMainBinding
    private lateinit var viewModel:ProductViewModel
    private val vmStore: StoreViewModel by activityViewModels()
    private val categoryListAdapter =CategoryListAdapter(arrayListOf())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("main fragment", "on create view")
        binding =FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("main fragment", "on view created")
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)

        vmStore.getOpenClose(requireActivity())
        viewModel.getAll()
        binding.recView.layoutManager = LinearLayoutManager(context)
        binding.recView.adapter = categoryListAdapter
        binding.textStoreClosed.visibility = View.GONE

        val (accToken,_) = getAccToken(requireActivity())
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
                        notLoggedIn()
                    }else{
                        Log.d("exp token", "not expired yet")
                        loggerIn()
                    }
                }
            }else{
               notLoggedIn()
            }
        }
        binding.shimmerLayout.startShimmer()
        binding.recView.visibility = View.GONE

        binding.buttonLogin.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
        binding.buttonHistory.setOnClickListener {
            val action = MainFragmentDirections.actionHistoryList()
            Navigation.findNavController(it).navigate(action)
        }
        binding.buttonNotification.setOnClickListener {
            val action = MainFragmentDirections.actionToListNotification()
            Navigation.findNavController(it).navigate(action)
        }
        binding.refreshLayout.setOnRefreshListener {
            viewModel.getAll()
            vmStore.getOpenClose(requireActivity())
            binding.skeletonLayout.visibility = View.VISIBLE
            binding.shimmerLayout.startShimmer()
            binding.recView.visibility = View.GONE
            binding.recView.layoutManager = LinearLayoutManager(context)
            binding.recView.adapter = categoryListAdapter
            binding.refreshLayout.isRefreshing = false
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.categoriesLD.observe(viewLifecycleOwner, Observer {
            categoryListAdapter.updatecategoryList(it)
            binding.shimmerLayout.stopShimmer()
            binding.skeletonLayout.visibility = View.GONE
            binding.recView.visibility = View.VISIBLE
        })
        vmStore.store.observe(viewLifecycleOwner){
            if (it.isSuccess){
                val store = it.data!!
                val open = store.open
                val close = store.close

                Log.d("open",open)
                Log.d("close",close)

                val currentTime = LocalTime.now()
                Log.d("currenttime",currentTime.toString())

                val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                val parsedTimeOpen = LocalTime.parse(open, formatter)
                val parsedTimeClose = LocalTime.parse(close, formatter)

                if (currentTime.isBefore(parsedTimeOpen) || currentTime.isAfter(parsedTimeClose)){
                    binding.textStoreClosed.visibility = View.VISIBLE
                } else {
                    binding.textStoreClosed.visibility = View.GONE
                }
            }
        }
    }

    private fun loggerIn(){
        binding.buttonHistory.visibility = View.VISIBLE
        binding.buttonNotification.visibility = View.VISIBLE
        binding.buttonLogin.visibility = View.GONE
    }
    private fun notLoggedIn(){
        binding.buttonHistory.visibility = View.GONE
        binding.buttonNotification.visibility = View.GONE
        binding.buttonLogin.visibility = View.VISIBLE
    }
}
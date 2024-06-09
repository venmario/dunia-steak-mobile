package com.example.restoapp.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
import java.util.Date

class MainFragment : Fragment() {
    private lateinit var binding:FragmentMainBinding
    private lateinit var viewModel:ProductViewModel
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

        viewModel.getAll()
        binding.recView.layoutManager = LinearLayoutManager(context)
        binding.recView.adapter = categoryListAdapter

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
            binding.progressLoad.visibility = View.VISIBLE
            binding.recView.layoutManager = LinearLayoutManager(context)
            binding.recView.adapter = categoryListAdapter
            binding.refreshLayout.isRefreshing = false
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.categoriesLD.observe(viewLifecycleOwner, Observer {
            categoryListAdapter.updatecategoryList(it)
            binding.progressLoad.visibility = View.GONE
        })
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
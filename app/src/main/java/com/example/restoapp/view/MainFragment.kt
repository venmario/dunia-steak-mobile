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
import androidx.recyclerview.widget.LinearLayoutManager
import com.auth0.android.jwt.JWT
import com.example.restoapp.R
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
        binding =FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)

        viewModel.getAll(requireActivity())
        binding.recView.layoutManager = LinearLayoutManager(context)
        binding.recView.adapter = categoryListAdapter

        val (accToken,username) = getAccToken(requireActivity())
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
                        loggerIn(username!!)
                    }
                }
            }else{
               notLoggedIn()
            }
        }
        binding.buttonLogin.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
        observeViewModel()

    }

    private fun observeViewModel() {
        viewModel.categoriesLD.observe(viewLifecycleOwner, Observer {
            categoryListAdapter.updatecategoryList(it)
        })
    }

    private fun loggerIn(username:String){
        binding.buttonHistory.visibility = View.VISIBLE
        binding.buttonNotification.visibility = View.VISIBLE
        binding.buttonLogin.visibility = View.GONE
        binding.textName.text = username
    }
    private fun notLoggedIn(){
        binding.buttonHistory.visibility = View.GONE
        binding.buttonNotification.visibility = View.GONE
        binding.buttonLogin.visibility = View.VISIBLE
        binding.textName.text = getString(R.string.penawaran)
    }
}
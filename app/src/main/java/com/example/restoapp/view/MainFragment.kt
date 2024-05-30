package com.example.restoapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restoapp.adapter.ProductListAdapter
import com.example.restoapp.databinding.FragmentMainBinding
import com.example.restoapp.viewmodel.ProductViewModel

class MainFragment : Fragment() {
    private lateinit var binding:FragmentMainBinding
    private lateinit var viewModel:ProductViewModel
    private val productListAdapter =ProductListAdapter(arrayListOf())
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
        binding.recView.adapter = productListAdapter
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.productsLD.observe(viewLifecycleOwner, Observer {
            productListAdapter.updateProductList(it)
        })
    }
}
package com.example.restoapp.view.poin

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restoapp.adapter.CategoryListPoinAdapter
import com.example.restoapp.databinding.FragmentPoinOrderBinding
import com.example.restoapp.viewmodel.ProductViewModel
import com.example.restoapp.viewmodel.StoreViewModel

@RequiresApi(Build.VERSION_CODES.O)
class PoinOrderFragment : Fragment() {
    private lateinit var binding: FragmentPoinOrderBinding
    private lateinit var viewModel: ProductViewModel
    private val vmStore: StoreViewModel by activityViewModels()
    private val categoryListAdapter = CategoryListPoinAdapter(arrayListOf())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPoinOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)

        vmStore.getOpenClose(requireActivity())
        viewModel.getAll()

        with(binding){
            recViewPoint.layoutManager = LinearLayoutManager(context)
            recViewPoint.adapter = categoryListAdapter
            recViewPoint.visibility = View.GONE
            shimmerLayout.startShimmer()
            textStoreClosed.visibility = View.GONE
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.categoriesLD.observe(viewLifecycleOwner) {
            categoryListAdapter.updatecategoryPoinList(it)
            with(binding){
                shimmerLayout.stopShimmer()
                skeletonLayout.visibility = View.GONE
                recViewPoint.visibility = View.VISIBLE
            }
        }
        vmStore.store.observe(viewLifecycleOwner){
            if (it.isSuccess){
                val store = it.data!!
                val isOpen = store.isOpen

                if (!isOpen){
                    binding.textStoreClosed.visibility = View.VISIBLE
                } else {
                    binding.textStoreClosed.visibility = View.GONE
                }
            }
        }
    }
}
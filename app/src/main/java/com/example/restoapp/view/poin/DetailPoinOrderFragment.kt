package com.example.restoapp.view.poin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.restoapp.databinding.FragmentDetailPoinOrderBinding
import com.example.restoapp.util.loadImage
import com.example.restoapp.viewmodel.OrderViewModel
import com.example.restoapp.viewmodel.ProductViewModel


class DetailPoinOrderFragment : Fragment() {
    private lateinit var binding: FragmentDetailPoinOrderBinding
    private lateinit var viewModel: ProductViewModel
    private val vmOrder: OrderViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailPoinOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        val productId = DetailPoinOrderFragmentArgs.fromBundle(requireArguments()).productId
        viewModel.getProductById(productId)

        with(binding){
            shimmerLayout.startShimmer()
            productView.visibility = View.GONE
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.productLD.observe(viewLifecycleOwner){
            if (it != null){
                val point = it.poin
                var total = 1
                with(binding){
                    shimmerLayout.stopShimmer()
                    shimmerLayout.visibility = View.GONE
                    productView.visibility = View.VISIBLE
                    imageViewProductDetail.loadImage(it.image, progressImageCard)
                    textProductDetailName.text = it.name
                    textProductDetailDesc.text = it.description
                    textProductDetailPoint.text = "${it.poin} pts"
                    buttonAddToCart.text = "Redeem ${it.poin} pts"

                    buttonRemove.setOnClickListener {
                        if(total == 1){
                            val action = DetailPoinOrderFragmentDirections.actionBackFromDetailPoint()
                            Navigation.findNavController(it).navigate(action)
                        }else{
                            total-=1
                            calculatePoint(point, total)
                        }
                    }
                    buttonAdd.setOnClickListener {
                        total+=1
                        calculatePoint(point, total)
                    }

                    buttonAddToCart.setOnClickListener { btn->
                        val note = textNote.text.toString()
                        vmOrder.selectProductPoint(it, total, note)
                        val action = DetailPoinOrderFragmentDirections.actionConfirmOrderPoint()
                        Navigation.findNavController(btn).navigate(action)
                    }
                }
            }
        }
    }

    private fun calculatePoint(point:Int, total:Int){
        val totalPoint= point*total
        binding.buttonAddToCart.text = "Redeem ${totalPoint} pts"
        binding.textCount.text = total.toString()
    }
}
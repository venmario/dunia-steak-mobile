package com.example.restoapp.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.restoapp.databinding.FragmentDetailProductBinding
import com.example.restoapp.global.GlobalData
import com.example.restoapp.model.OrderDetail
import com.example.restoapp.model.Product
import com.example.restoapp.util.loadImage
import com.example.restoapp.viewmodel.OrderViewModel
import com.example.restoapp.viewmodel.ProductViewModel
import com.google.gson.Gson

class ProductDetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailProductBinding
    private lateinit var viewModel: ProductViewModel
    private lateinit var viewModelOrder: OrderViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        viewModelOrder = ViewModelProvider(this).get(OrderViewModel::class.java)
        val productId = ProductDetailFragmentArgs.fromBundle(requireArguments()).productId
        viewModel.getProductById(productId,requireActivity())

        var price:Int?=null
        var productSelected:Product?=null
        viewModel.productLD.observe(viewLifecycleOwner, Observer {
            with(binding){
                imageViewProductDetail.loadImage(it.image, progressImageCard)
                textProductDetailName.text = it.name
                textProductDetailDesc.text = it.description
                textProductDetailPrice.text = "Rp${it.price}.000"
                buttonAddToCart.text = "Tambah - Rp${it.price}.000"
                price = it.price
                productSelected = it
            }
        })
        with(binding){
            val note:String = textNote.text.toString()
            var total = 1
            buttonRemove.setOnClickListener {
                if(total == 1){
                    val action = ProductDetailFragmentDirections.actionBackToMain()
                    Navigation.findNavController(it).navigate(action)
                }else{
                    price.let {itPrice ->
                        total-=1
                        calculatePrice(itPrice!!, total)
                    }
                }
            }
            buttonAdd.setOnClickListener {
                total+=1
                price.let {itPrice ->
                    calculatePrice(itPrice!!, total)
                }
            }

            buttonAddToCart.setOnClickListener {
                val orderDetails = GlobalData.orderDetail
                val iterator = orderDetails.iterator()
                productSelected.let {
                    Log.d("product selected", productSelected.toString())
                    var found = false
                    while (iterator.hasNext()) {
                        val orderDetail = iterator.next()
                        if (orderDetail.product.id == productSelected!!.id) {
                            orderDetail.quantity+=total
                            found = true
                            break
                        }
                    }
                    if(!found){
                        orderDetails.add(OrderDetail(productSelected!!,
                            productSelected!!.price, productSelected!!.poin,total,note))
                        Log.d("order details", Gson().toJson(orderDetails))
                    }
                    requireParentFragment().childFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun calculatePrice(price:Int, total:Int){
        val totalPrice = price*total
        binding.buttonAddToCart.text = "Tambah - Rp${totalPrice}.000"
        binding.textCount.text = total.toString()
    }
}
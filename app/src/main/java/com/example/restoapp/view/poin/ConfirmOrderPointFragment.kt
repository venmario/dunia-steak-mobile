package com.example.restoapp.view.poin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.restoapp.R
import com.example.restoapp.databinding.FragmentConfirmOrderPointBinding
import com.example.restoapp.model.OrderDetail
import com.example.restoapp.util.getUserPoint
import com.example.restoapp.viewmodel.OrderViewModel
import com.example.restoapp.viewmodel.TransactionViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.common.base.Strings.isNullOrEmpty

class ConfirmOrderPointFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentConfirmOrderPointBinding
    private val viewModel: OrderViewModel by activityViewModels()
    private lateinit var vmTrans: TransactionViewModel
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConfirmOrderPointBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vmTrans = ViewModelProvider(this).get(TransactionViewModel::class.java)
        navController = Navigation.findNavController(requireParentFragment().requireView())
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.selectedProductPoint.observe(viewLifecycleOwner){
            if (it!=null){
                with(binding){
                    textProductTotal.text = "${it.product.name} x${it.total}"
                    textPointPerProduct.text = "${it.product.poin} pts"
                    val totalPoints = it.product.poin * it.total
                    textTotalPoints.text = "$totalPoints pts"
                    val userPoints = getUserPoint(requireActivity())?.toInt()
                    textYourPoints.text = "$userPoints pts"

                    if(!isNullOrEmpty(it.note)){
                        textNote.visibility = View.VISIBLE
                        textNote.text = "Note :\n${it.note}"
                    }

                    if(totalPoints > userPoints!!){
                        textInsufficient.visibility = View.VISIBLE
                        disableButton()
                    }

                    buttonOrder.setOnClickListener {_->
                        buttonOrder.text = "Loading..."
                        val orderDetails = ArrayList<OrderDetail>()
                        val orderDetail = OrderDetail(it.product,0,it.product.poin, it.total, it.note)
                        orderDetails.add(orderDetail)

                        vmTrans.redeemPoint(requireActivity(),orderDetails)
                        disableButton()
                    }

                    buttonCancel.setOnClickListener {
                        navController.popBackStack()
                    }
                }
            }
        }

        vmTrans.redeemPointResult.observe(viewLifecycleOwner){
            if (it!=null){
                if (it.isSuccess){
                    Log.d("point order","success")
                    val action = ConfirmOrderPointFragmentDirections.actionConfirmOrderPointToHistory(it.data!!)
                    navController.navigate(action)
                }else{
                    Log.d("error point order" , it.errorMessage.toString())
                    binding.buttonOrder.text = "Something went wrong..."
                }
            }
        }
    }

    private fun disableButton() {
        with(binding){
            buttonOrder.isEnabled = false
            buttonOrder.setTextColor(resources.getColorStateList(R.color.md_theme_secondary_disable))
            buttonOrder.backgroundTintList = resources.getColorStateList(R.color.md_theme_primary_disable)
        }
    }
}
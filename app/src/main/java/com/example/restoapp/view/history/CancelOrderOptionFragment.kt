package com.example.restoapp.view.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.restoapp.R
import com.example.restoapp.databinding.FragmentCancelOrderOptionBinding
import com.example.restoapp.viewmodel.OrderViewModel
import com.example.restoapp.viewmodel.TransactionViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CancelOrderOptionFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentCancelOrderOptionBinding
    private lateinit var navController: NavController
    private val viewModel: OrderViewModel by activityViewModels()
    private lateinit var vmTrans: TransactionViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCancelOrderOptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(requireParentFragment().requireView())
        vmTrans = ViewModelProvider(this).get(TransactionViewModel::class.java)
        observeViewModel(view)
    }

    private fun observeViewModel(view: View) {
        viewModel.cancelOrderLD.observe(viewLifecycleOwner){
            Log.d("cancelOrder",it.toString())
            with(binding){
                buttonCancel.setOnClickListener {_->
                    navController.popBackStack()
                }
                buttonConfirm.setOnClickListener {_->
                    disableButton()
                    buttonConfirm.text = "Loading..."
                    val checkedId = radioCancelParent.checkedRadioButtonId
                    val checkedRadio = view.findViewById<RadioButton>(checkedId)
                    val reason = checkedRadio.text.toString()

                    vmTrans.cancelTransaction(requireActivity(), it, reason)
                }
            }
        }

        vmTrans.cancelOrderLD.observe(viewLifecycleOwner){
            if (it.isSuccess){
                val action = CancelOrderOptionFragmentDirections.actionBackFromCancelOption(it.data!!.transactionId!!)
                navController.navigate(action)
            }else{
                Log.d("cancel error",it.errorMessage.toString())
                binding.buttonConfirm.text = "Something went wrong..."
            }
        }
    }

    private fun disableButton() {
        with(binding){
            buttonConfirm.isEnabled = false
            buttonConfirm.setTextColor(resources.getColorStateList(R.color.md_theme_secondary_disable))
            buttonConfirm.backgroundTintList = resources.getColorStateList(R.color.md_theme_primary_disable)
        }
    }
}
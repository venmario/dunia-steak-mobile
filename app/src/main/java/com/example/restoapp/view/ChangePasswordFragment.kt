package com.example.restoapp.view

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.restoapp.R
import com.example.restoapp.databinding.FragmentChangePasswordBinding
import com.example.restoapp.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ChangePasswordFragment : Fragment() {
    private lateinit var binding: FragmentChangePasswordBinding
    private val viewModel: AuthViewModel by activityViewModels()
    private var isCurrentPasswordError = true
    private var isNewPasswordError = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disableButton()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.userLD.observe(viewLifecycleOwner){
            if(it != null){
                with(binding){
                    buttonSave.setOnClickListener {_->
                        val currentPassword = password.text.toString()
                        val newPassword = newPassword.text.toString()

                        viewModel.updateUserPassword(requireActivity(), it.username!!, currentPassword, newPassword)
                    }

                    passwordLayout.editText?.doOnTextChanged{ text, _, _, _ ->
                        if (!TextUtils.isEmpty(text)){
                            if (text?.length!! < 6) {
                                passwordLayout.error = "Minimal 6 digits password"
                                isCurrentPasswordError = true
                                disableButton()
                            }
                            else {
                                passwordLayout.error = null
                                passwordLayout.isErrorEnabled = false
                                isCurrentPasswordError = false
                                enableButton()
                            }
                        }else{
                            passwordLayout.error = "Please input your password"
                            isCurrentPasswordError = true
                            disableButton()
                        }
                    }

                    newPasswordLayout.editText?.doOnTextChanged{ text, _, _, _ ->
                        if (!TextUtils.isEmpty(text)){
                            if (text?.length!! < 6) {
                                newPasswordLayout.error = "Minimal 6 digits password"
                                isNewPasswordError = true
                                disableButton()
                            }
                            else {
                                newPasswordLayout.error = null
                                newPasswordLayout.isErrorEnabled = false
                                isNewPasswordError = false
                                enableButton()
                            }
                        }else{
                            newPasswordLayout.error = "Please input your password"
                            isNewPasswordError = true
                            disableButton()
                        }
                    }
                }
            }
        }

        viewModel.updatePasswordLD.observe(viewLifecycleOwner){
            with(binding){
                if (it.success){
                    Snackbar.make(rootLayout, it.successMessage!!, Snackbar.LENGTH_SHORT)
                        .show()
                }else{
                    when(it.code){
                        400 ->{
                            val errMsg = it.errorMessage
                            val type = object : TypeToken<Map<String, List<String>>>() {}.type
                            val errors: Map<String, List<String>> = Gson().fromJson(errMsg, type)
                            if (errors.containsKey("currentPassword")){
                                passwordLayout.error = errors["currentPassword"]?.get(0)
                                isCurrentPasswordError = true
                            }
                            if (errors.containsKey("newPassword")){
                                newPasswordLayout.error = errors["newPassword"]?.get(0)
                                isNewPasswordError = true
                            }
                            disableButton()
                        }
                        404 ->{
                            passwordLayout.error = it.errorMessage
                            isCurrentPasswordError = true
                        }
                        else -> {
                            Snackbar.make(rootLayout, it.errorMessage!!, Snackbar.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun disableButton() {
        with(binding) {
            buttonSave.backgroundTintList =
                resources.getColorStateList(R.color.md_theme_primary_disable)
            buttonSave.setTextColor(resources.getColorStateList(R.color.md_theme_secondary_disable))
            buttonSave.isEnabled = false
        }
    }

    private fun enableButton() {
        with(binding) {
            if (!isCurrentPasswordError && !isNewPasswordError){
                buttonSave.backgroundTintList =
                    resources.getColorStateList(R.color.md_theme_primary)
                buttonSave.setTextColor(resources.getColorStateList(R.color.md_theme_secondary))
                buttonSave.isEnabled = true
            }
        }
    }
}
package com.example.restoapp.view

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.restoapp.R
import com.example.restoapp.databinding.FragmentEditProfileBinding
import com.example.restoapp.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private val viewmodel: AuthViewModel by activityViewModels()
    private var isFirstnameError = true
    private var isLastnameError = true
    private var isPhoneNumberError = true
    private var isEmailError = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disableButton()
        with(binding) {
            firstNameLayout.editText?.doOnTextChanged { text, _, _, _ ->
                if (TextUtils.isEmpty(text)) {
                    firstNameLayout.error = "Please input your first name"
                    isFirstnameError = true
                    disableButton()
                } else {
                    firstNameLayout.error = null
                    firstNameLayout.isErrorEnabled = false
                    isFirstnameError = false
                    enableButton()
                }
            }
            lastNameLayout.editText?.doOnTextChanged { text, _, _, _ ->
                if (TextUtils.isEmpty(text)) {
                    lastNameLayout.error = "Please input your last name"
                    isLastnameError = true
                    disableButton()
                } else {
                    lastNameLayout.error = null
                    lastNameLayout.isErrorEnabled = false
                    isLastnameError = false
                    enableButton()
                }
            }

            phoneNumberLayout.editText?.doOnTextChanged { text, _, _, _ ->
                if (!TextUtils.isEmpty(text)) {
                    if (text?.length!! < 11) {
                        phoneNumberLayout.error = "At least there are 11 digits"
                        isPhoneNumberError = true
                        disableButton()
                    } else if (text.length > 12) {
                        phoneNumberLayout.error = "Maximal phone number digits are 12"
                        isPhoneNumberError = true
                        disableButton()
                    } else {
                        phoneNumberLayout.error = null
                        phoneNumberLayout.isErrorEnabled = false
                        isPhoneNumberError = false
                        enableButton()
                    }
                } else {
                    phoneNumberLayout.error = "Please input phone number"
                    isPhoneNumberError = true
                    disableButton()
                }
            }

            emailLayout.editText?.doOnTextChanged { text, _, _, _ ->
                if (!TextUtils.isEmpty(text)) {
                    if (!Patterns.EMAIL_ADDRESS.matcher(text!!).matches()) {
                        emailLayout.error = "Wrong email address!"
                        isEmailError = true
                        disableButton()
                    } else {
                        emailLayout.error = null
                        emailLayout.isErrorEnabled = false
                        isEmailError = false
                        enableButton()
                    }
                } else {
                    emailLayout.error = "Please input your email"
                    isEmailError = true
                    disableButton()
                }
            }
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewmodel.userLD.observe(viewLifecycleOwner){
            if (it != null){
                with(binding){

                    val user = it
                    firstName.setText(user.firstname)
                    lastName.setText(user.lastname)
                    phoneNumber.setText(user.phonenumber)
                    email.setText(user.email)
                    username.setText(user.username)

                    buttonSave.setOnClickListener {
                        var newfirstName:String? = null
                        var newlastName:String? = null
                        var newphoneNumber:String? = null
                        var newemail:String? = null
                        if (firstName.text.toString() != user.firstname){
                            newfirstName = firstName.text.toString()
                        }
                        if (lastName.text.toString() != user.lastname){
                            newlastName = lastName.text.toString()
                        }
                        if (phoneNumber.text.toString() != user.phonenumber){
                            newphoneNumber = phoneNumber.text.toString()
                        }
                        if (email.text.toString() != user.email){
                            newemail = email.text.toString()
                        }
                        viewmodel.updateUser(requireActivity(), user.username!!, newfirstName, newlastName, newemail, newphoneNumber)
                    }
                }
            }
        }
        viewmodel.updateUserLD.observe(viewLifecycleOwner){
            if (it != null){
                with(binding){
                    Log.d("code",it.code.toString())
                    if (it.code == 202){
                        Snackbar.make(rootLayout, it.successMessage!!, Snackbar.LENGTH_SHORT)
                            .show()
                    }else if (it.code == 400){
                        val errMsg = it.errorMessage
                        val type = object : TypeToken<Map<String, List<String>>>() {}.type
                        val errors: Map<String, List<String>> = Gson().fromJson(errMsg, type)
                        if (errors.containsKey("phonenumber")){
                            phoneNumberLayout.error = errors["phonenumber"]?.get(0)
                            isPhoneNumberError = true
                        }
                        if (errors.containsKey("email")){
                            emailLayout.error = errors["email"]?.get(0)
                            isEmailError = true
                        }
                        disableButton()
                    }else if(it.code == 500){
                        Snackbar.make(rootLayout, it.errorMessage!!, Snackbar.LENGTH_SHORT)
                            .show()
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
            if (!isLastnameError && !isFirstnameError && !isPhoneNumberError && !isEmailError){
                buttonSave.backgroundTintList =
                    resources.getColorStateList(R.color.md_theme_primary)
                buttonSave.setTextColor(resources.getColorStateList(R.color.md_theme_secondary))
                buttonSave.isEnabled = true
            }
        }
    }
}
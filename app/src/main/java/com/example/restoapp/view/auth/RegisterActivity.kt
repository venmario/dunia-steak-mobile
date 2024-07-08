package com.example.restoapp.view.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.restoapp.R
import com.example.restoapp.databinding.ActivityRegisterBinding
import com.example.restoapp.model.User
import com.example.restoapp.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: AuthViewModel

    private var isFirstnameError = true
    private var isLastnameError = true
    private var isPhoneNumberError = true
    private var isEmailError = true
    private var isUsernameError = true
    private var isPasswordError = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        disableButton()
        with(binding){
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
                if (!TextUtils.isEmpty(text)){
                    if (text?.length!! < 11) {
                        phoneNumberLayout.error = "At least there are 11 digits"
                        isPhoneNumberError = true
                        disableButton()
                    }else if (text.length > 12) {
                        phoneNumberLayout.error = "Maximal phone number digits are 12"
                        isPhoneNumberError = true
                        disableButton()
                    }
                    else {
                        phoneNumberLayout.error = null
                        phoneNumberLayout.isErrorEnabled = false
                        isPhoneNumberError = false
                        enableButton()
                    }
                }else{
                    phoneNumberLayout.error = "Please input phone number"
                    isPhoneNumberError = true
                    disableButton()
                }
            }

            emailLayout.editText?.doOnTextChanged{ text, _, _, _ ->
                if (!TextUtils.isEmpty(text)){
                    if (!Patterns.EMAIL_ADDRESS.matcher(text!!).matches()) {
                        emailLayout.error = "Wrong email address!"
                        isEmailError = true
                        disableButton()
                    }
                    else {
                        emailLayout.error = null
                        emailLayout.isErrorEnabled = false
                        isEmailError = false
                        enableButton()
                    }
                }else{
                    emailLayout.error = "Please input your email"
                    isEmailError = true
                    disableButton()
                }
            }

            usernameLayout.editText?.doOnTextChanged{ text, _, _, _ ->
                if (!TextUtils.isEmpty(text)){
                    if (text?.length!! < 6) {
                        usernameLayout.error = "Minimal 6 digits username"
                        isUsernameError = true
                        disableButton()
                    }
                    else {
                        usernameLayout.error = null
                        usernameLayout.isErrorEnabled = false
                        isUsernameError = false
                        enableButton()
                    }
                }else{
                    usernameLayout.error = "Please input your username"
                    isUsernameError = true
                    disableButton()
                }
            }

            passwordLayout.editText?.doOnTextChanged{ text, _, _, _ ->
                if (!TextUtils.isEmpty(text)){
                    if (text?.length!! < 6) {
                        passwordLayout.error = "Minimal 6 digits password"
                        isPasswordError = true
                        disableButton()
                    }
                    else {
                        passwordLayout.error = null
                        passwordLayout.isErrorEnabled = false
                        isPasswordError = false
                        enableButton()
                    }
                }else{
                    passwordLayout.error = "Please input your password"
                    isPasswordError = true
                    disableButton()
                }
            }

            buttonRegister.setOnClickListener {btn->
                val firstname = firstName.text.toString()
                val lastname = lastName.text.toString()
                val email = email.text.toString()
                val username = username.text.toString()
                val password = password.text.toString()
                val phonenumber = phoneNumber.text.toString()

                val user= User(0,username,firstname,lastname,phonenumber,email,password,0,"")
                viewModel.register(user)
                viewModel.registerResponse.observe(this@RegisterActivity) {
                    if (it.success) {
                        Snackbar.make(binding.rootLayout, "Register succeed", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Sign In"){
                                startActivity(Intent(applicationContext,LoginActivity::class.java))
                                finish()
                            }
                            .setBackgroundTint(resources.getColor(R.color.md_theme_secondary))
                            .setActionTextColor(resources.getColor(R.color.md_theme_primary))
                            .show()
                    } else {
                        val errMsg = it.errorMessage
                        if (it.code == 400){
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
                            if (errors.containsKey("username")){
                                usernameLayout.error = errors["username"]?.get(0)
                                isUsernameError = true
                            }
                            if (errors.containsKey("password")){
                                passwordLayout.error = errors["password"]?.get(0)
                                isPasswordError = true
                            }
                            disableButton()
                        }else{
                            Snackbar.make(binding.rootLayout, errMsg.toString(), Snackbar.LENGTH_SHORT)
                                .show()
                        }

                    }
                }
            }
            txtSignIn.setOnClickListener {
                startActivity(Intent(applicationContext,LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun disableButton() {
        with(binding) {
            buttonRegister.backgroundTintList =
                resources.getColorStateList(com.example.restoapp.R.color.md_theme_primary_disable)
            buttonRegister.setTextColor(resources.getColorStateList(com.example.restoapp.R.color.md_theme_secondary_disable))
            buttonRegister.isEnabled = false
        }
    }

    private fun enableButton() {
        with(binding) {
            if (!isLastnameError && !isFirstnameError && !isPhoneNumberError && !isEmailError && !isUsernameError && !isPasswordError){
                buttonRegister.backgroundTintList =
                    resources.getColorStateList(com.example.restoapp.R.color.md_theme_primary)
                buttonRegister.setTextColor(resources.getColorStateList(com.example.restoapp.R.color.md_theme_secondary))
                buttonRegister.isEnabled = true
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return super.onKeyDown(keyCode, event)
    }
}
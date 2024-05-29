package com.example.restoapp.view.auth

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.restoapp.databinding.ActivityRegisterBinding
import com.example.restoapp.model.User
import com.example.restoapp.viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        with(binding){
            buttonRegister.setOnClickListener {
                val firstname = firstName.text.toString()
                val lastname = lastName.text.toString()
                val email = email.text.toString()
                val username = username.text.toString()
                val password = password.text.toString()
                val phonenumber = phoneNumber.text.toString()

                val user= User(0,username,firstname,lastname,phonenumber,email,password,0,"")
                viewModel.register(user)
                viewModel.registerSuccess.observe(this@RegisterActivity) {
                    if (it) {
                        Toast.makeText(applicationContext, "Register succeed!", Toast.LENGTH_LONG).show()
                        startActivity(Intent(applicationContext,LoginActivity::class.java))
                        finish()
                    } else {
                        val errMsg = viewModel.errorMessage.value
                        Toast.makeText(applicationContext, errMsg, Toast.LENGTH_LONG).show()

                    }
                }
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
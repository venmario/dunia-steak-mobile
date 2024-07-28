package com.example.restoapp.view.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.restoapp.databinding.ActivityLoginBinding
import com.example.restoapp.util.getFcmTokens
import com.example.restoapp.view.MainActivity
import com.example.restoapp.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    companion object{
        val ACCESS_TOKEN = "ACCESS_TOKEN"
        val USERNAME = "USERNAME"
    }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel
    lateinit var shared: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        var sharedFile = applicationContext.packageName
        shared = applicationContext.getSharedPreferences(sharedFile, Context.MODE_PRIVATE)
        with(binding){

            buttonSignIn.setOnClickListener {
                val email = editTextUsername.text.toString()
                val password = editTextPassword.text.toString()
                val (oldFcmToken, currentFcmToken) = getFcmTokens(this@LoginActivity)
                if (oldFcmToken != null && currentFcmToken != null) {
                    Log.d("sign in","sign in")
                    viewModel.signIn(email,password,oldFcmToken,currentFcmToken)
                }

                viewModel.loginResponse.observe(this@LoginActivity, Observer{
                    if(it.success){
                        val editor: SharedPreferences.Editor = shared.edit()
                        editor.putString(ACCESS_TOKEN,it.accToken)
                        editor.putString(USERNAME,it.username)
                        editor.apply()
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }else{
                        Snackbar.make(binding.rootLayout, it.errorMessage!!, Snackbar.LENGTH_SHORT)
                            .show()
                    }
                })
            }

            txtSignUp.setOnClickListener{
                startActivity(Intent(applicationContext,RegisterActivity::class.java))
            }

        }
    }
}

package com.example.restoapp.view.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.restoapp.databinding.ActivityLoginBinding
import com.example.restoapp.util.showToast
import com.example.restoapp.view.MainActivity
import com.example.restoapp.viewmodel.AuthViewModel

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
                viewModel.signIn(email,password)

                viewModel.loginResponse.observe(this@LoginActivity, Observer{
                    if(it.success){
                        val editor: SharedPreferences.Editor = shared.edit()
                        editor.putString(ACCESS_TOKEN,it.accToken)
                        editor.putString(USERNAME,it.username)
                        editor.apply()
                        startActivity(Intent(applicationContext,MainActivity::class.java))
                        finish()
                    }else{
                        showToast(it.errorMessage!!,applicationContext)
                    }
                })
            }

            txtSignUp.setOnClickListener{
                startActivity(Intent(applicationContext,RegisterActivity::class.java))
            }

        }


//        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
//            .get(LoginViewModel::class.java)
//
//        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
//            val loginState = it ?: return@Observer
//
//            // disable login button unless both username / password is valid
//            login.isEnabled = loginState.isDataValid
//
//            if (loginState.usernameError != null) {
//                username.error = getString(loginState.usernameError)
//            }
//            if (loginState.passwordError != null) {
//                password.error = getString(loginState.passwordError)
//            }
//        })
//
//        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
//            val loginResult = it ?: return@Observer
//
//            loading.visibility = View.GONE
//            if (loginResult.error != null) {
//                showLoginFailed(loginResult.error)
//            }
//            if (loginResult.success != null) {
//                updateUiWithUser(loginResult.success)
//            }
//            setResult(Activity.RESULT_OK)
//
//            //Complete and destroy login activity once successful
//            finish()
//        })
    }

//    private fun updateUiWithUser(model: LoggedInUserView) {
//        val welcome = getString(R.string.welcome)
//        val displayName = model.displayName
//        // TODO : initiate successful logged in experience
//        Toast.makeText(
//            applicationContext,
//            "$welcome $displayName",
//            Toast.LENGTH_LONG
//        ).show()
//    }

}

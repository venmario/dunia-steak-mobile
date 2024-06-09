package com.example.restoapp.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.auth0.android.jwt.JWT
import com.example.restoapp.databinding.FragmentProfileBinding
import com.example.restoapp.util.clearNotifications
import com.example.restoapp.util.getAccToken
import com.example.restoapp.util.getFcmTokens
import com.example.restoapp.util.setNewAccToken
import com.example.restoapp.view.auth.LoginActivity
import com.example.restoapp.viewmodel.AuthViewModel
import java.util.Date

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewmodel: AuthViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel = ViewModelProvider(this).get(AuthViewModel::class.java)

        loggedOut()
        val (accToken) = getAccToken(requireActivity())
        accToken?.let {
            if (it.isNotEmpty()){
                val expToken = JWT(it).expiresAt
                if (expToken != null) {
                    Log.d("exp token", expToken.time.toString())
                    Log.d("now", Date().time.toString())
                    //acctoken expired
                    if( Date().time > expToken.time){
                        Log.d("exp token", "token expired")
                        setNewAccToken(requireActivity(),"","")
                    }else{
                        Log.d("exp token", "not expired yet")
                        viewmodel.getUser(requireActivity())
                        observeViewModel()
                    }
                }
            }
        }

        binding.textLogout.setOnClickListener {
            val (_,currentFcmToken) = getFcmTokens(requireActivity())
            viewmodel.logout(requireActivity(),currentFcmToken)
        }
        binding.textSignIn.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
    }

    fun observeViewModel(){
        viewmodel.userLD.observe(viewLifecycleOwner){
            //set profile
            if (it != null){
                binding.textName.text = "Hello ${it.firstname?.capitalize() }"
                binding.textPoint.text = "${it.poin} pts"
                loggedIn()
            }
        }
        viewmodel.logoutResponse.observe(viewLifecycleOwner){
            if (it != null){
                Log.d("logout","logout")
                setNewAccToken(requireActivity(),"","")
                clearNotifications()
                loggedOut()
            }
        }
    }

    fun loggedOut(){
        binding.textEditProfile.visibility = View.GONE
        binding.textLogout.visibility = View.GONE
        binding.textSignIn.visibility = View.VISIBLE
        binding.textPoint.text = "0 pts"
        binding.textName.text = "Hello, There!"
    }
    fun loggedIn(){
        binding.textEditProfile.visibility = View.VISIBLE
        binding.textLogout.visibility = View.VISIBLE
        binding.textSignIn.visibility = View.GONE
    }
}
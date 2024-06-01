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
import com.example.restoapp.util.getAccToken
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
                        startActivity(Intent(requireContext(),LoginActivity::class.java))
                    }else{
                        Log.d("exp token", "not expired yet")
                        viewmodel.getUser(requireActivity())
                        observeViewModel()
                    }
                }
            }else{
                startActivity(Intent(requireContext(),LoginActivity::class.java))
            }
        }
    }

    fun observeViewModel(){
        viewmodel.userLD.observe(viewLifecycleOwner){
            //set profile
            binding.txtBlank.text = "logged in ${it.username}"
        }
    }
}
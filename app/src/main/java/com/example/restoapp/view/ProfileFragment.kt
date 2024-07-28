package com.example.restoapp.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.auth0.android.jwt.JWT
import com.example.restoapp.databinding.FragmentProfileBinding
import com.example.restoapp.util.clearNotifications
import com.example.restoapp.util.getAccToken
import com.example.restoapp.util.getFcmTokens
import com.example.restoapp.util.setNewAccToken
import com.example.restoapp.util.setUserPoint
import com.example.restoapp.view.auth.LoginActivity
import com.example.restoapp.viewmodel.AuthViewModel
import java.util.Date

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewmodel: AuthViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                        binding.textSignIn.visibility = View.GONE
                        viewmodel.getUser(requireActivity())
                        observeViewModel()
                    }
                }
            }
        }

        with(binding){
            textLogout.setOnClickListener {
                val (_,currentFcmToken) = getFcmTokens(requireActivity())
                viewmodel.logout(requireActivity(),currentFcmToken)
            }
            textSignIn.setOnClickListener {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
            textHelpCenter.setOnClickListener {
                val action = ProfileFragmentDirections.actionHelpCenter()
                Navigation.findNavController(requireView()).navigate(action)
            }
            textTermsService.setOnClickListener {
                val action = ProfileFragmentDirections.actionTermService()
                Navigation.findNavController(requireView()).navigate(action)
            }
            textLanguage.setOnClickListener {txt->
                val action = ProfileFragmentDirections.actionLanguage()
                Navigation.findNavController(txt).navigate(action)
            }
        }

    }

    private fun observeViewModel(){
        viewmodel.userLD.observe(viewLifecycleOwner){
            //set profile
            if (it != null){
                val user = it
                binding.textName.text = "Hello ${user.firstname?.capitalize() }"
                binding.textPoint.text = "${user.poin} pts"
                setUserPoint(requireActivity(), user.poin)
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
        with(binding){
            textEditProfile.visibility = View.GONE
            textLogout.visibility = View.GONE
            textSignIn.visibility = View.VISIBLE
            textPoint.text = "0 pts"
            textName.text = "Hello, There!"
            pointCard.isClickable = false
            textSetting.visibility = View.GONE
        }
    }
    fun loggedIn(){
        with(binding){
            textEditProfile.visibility = View.VISIBLE
            textSetting.visibility = View.VISIBLE
            textLogout.visibility = View.VISIBLE
            textSignIn.visibility = View.GONE
            pointCard.isClickable = true
            pointCard.setOnClickListener {
                val action = ProfileFragmentDirections.actionPointProductList()
                Navigation.findNavController(requireView()).navigate(action)
            }
            textEditProfile.setOnClickListener {
                val action = ProfileFragmentDirections.actionEditProfile()
                Navigation.findNavController(requireView()).navigate(action)
            }
            textSetting.setOnClickListener {
                val action = ProfileFragmentDirections.actionToSetting()
                Navigation.findNavController(requireView()).navigate(action)
            }
        }
    }

    companion object{
        val POIN_USER = "POIN_USER"
    }
}
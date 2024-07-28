package com.example.restoapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.restoapp.databinding.FragmentLanguageOptionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LanguageOptionFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentLanguageOptionBinding
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLanguageOptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(requireParentFragment().requireView())
        with(binding){
            textEnglish.setOnClickListener {
                navController.popBackStack()
            }
        }
    }
}
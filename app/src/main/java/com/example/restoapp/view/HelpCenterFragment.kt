package com.example.restoapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.restoapp.databinding.FragmentHelpCenterBinding

class HelpCenterFragment : Fragment() {
    private lateinit var binding:FragmentHelpCenterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHelpCenterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            faqQuestion1.setOnClickListener {
                if (faqAnswer1.visibility == View.GONE){
                    faqAnswer1.visibility = View.VISIBLE
                }else{
                    faqAnswer1.visibility = View.GONE
                }
            }
            faqQuestion2.setOnClickListener {
                if (faqAnswer2.visibility == View.GONE){
                    faqAnswer2.visibility = View.VISIBLE
                }else{
                    faqAnswer2.visibility = View.GONE
                }
            }
        }
    }
}
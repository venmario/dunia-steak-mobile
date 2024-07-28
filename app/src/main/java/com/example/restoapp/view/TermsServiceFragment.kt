package com.example.restoapp.view

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.restoapp.R
import com.example.restoapp.databinding.FragmentTermsServiceBinding

class TermsServiceFragment : Fragment() {
    private lateinit var binding: FragmentTermsServiceBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTermsServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            val termsText = getString(R.string.terms_of_services_text)
            termsOfServicesContent.text = Html.fromHtml(termsText, Html.FROM_HTML_MODE_LEGACY)
        }
    }
}
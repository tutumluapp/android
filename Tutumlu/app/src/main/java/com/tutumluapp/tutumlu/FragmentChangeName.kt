package com.tutumluapp.tutumlu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tutumluapp.tutumlu.databinding.FragmentChangeNameBinding

class FragmentChangeName : Fragment() {

    private lateinit var binding:FragmentChangeNameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentChangeNameBinding.inflate(inflater,container,false)

        binding.hdrChangename.ttHdr.text = "Change Name"
        binding.hdrChangename.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return binding.root
    }
}
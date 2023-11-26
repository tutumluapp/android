package com.tutumluapp.tutumlu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tutumluapp.tutumlu.databinding.FragmentSearchBarcodeBinding


class FragmentSearchBarcode : Fragment() {

    private lateinit var binding:FragmentSearchBarcodeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBarcodeBinding.inflate(inflater,container,false)



        return binding?.root
    }


}
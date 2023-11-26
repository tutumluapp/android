package com.tutumluapp.tutumlu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.tutumluapp.tutumlu.databinding.FragmentProfileInfoBinding


class FragmentProfileInfo : Fragment() {

    private lateinit var binding: FragmentProfileInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileInfoBinding.inflate(inflater,container,false)

        binding.hdrProfile.ttHdr.text = "Profile"
        binding.hdrProfile.btnOptional.setImageResource(R.drawable.btn_exit)
        binding.hdrProfile.btnOptional.setOnClickListener {
            requireActivity().finishAffinity()
        }
        binding.hdrProfile.btnBack.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnChangename.setOnClickListener {

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame_profile, FragmentChangeName())
                .addToBackStack(null)
                .commit()

        }

        binding.btnChangepsw.setOnClickListener {

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame_profile, FragmentChangePsw())
                .addToBackStack(null)
                .commit()
        }

        return binding?.root

    }


}
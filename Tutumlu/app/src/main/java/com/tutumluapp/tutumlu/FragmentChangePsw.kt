package com.tutumluapp.tutumlu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tutumluapp.tutumlu.databinding.FragmentChangeNameBinding
import com.tutumluapp.tutumlu.databinding.FragmentChangePswBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentChangePsw.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentChangePsw : Fragment() {

    private lateinit var binding:FragmentChangePswBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChangePswBinding.inflate(inflater,container,false)

        binding.hdrChangepsw.ttHdr.text = "Change Password"
        binding.hdrChangepsw.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return binding.root
    }


}
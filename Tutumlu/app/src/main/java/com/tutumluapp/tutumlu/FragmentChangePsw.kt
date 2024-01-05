package com.tutumluapp.tutumlu

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.tutumluapp.tutumlu.databinding.FragmentChangeNameBinding
import com.tutumluapp.tutumlu.databinding.FragmentChangePswBinding
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.FilterOperation
import io.github.jan.supabase.postgrest.query.FilterOperator
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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

        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("Credentials", Context.MODE_PRIVATE)

        val supabaseUrl = "https://qbpruczdytiwqouzoztl.supabase.co"
        val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFicHJ1Y3pkeXRpd3FvdXpvenRsIiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTcxMzE0ODUsImV4cCI6MjAxMjcwNzQ4NX0.aWqvr3-qOnJGQXZwk4GVNVXRnd2OKMOn3fkWF8Pm2UE"

        val supabase = createSupabaseClient(supabaseUrl, supabaseKey) {
            install(GoTrue)
            install(Postgrest)
        }


        binding.hdrChangepsw.ttHdr.text = "Change Password"
        binding.hdrChangepsw.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnChangename.setOnClickListener {
            val oldpsw = binding.inputOldPsw.text.toString()
            val newpsw = binding.inputNewPsw.text.toString()
            val newpswAgain = binding.inputNewPswAgain.text.toString()
            if(!oldpsw.isNullOrEmpty() && !newpsw.isNullOrEmpty() && !newpswAgain.isNullOrEmpty()){
                if(newpsw != newpswAgain){
                    Toast.makeText(requireContext(), "Please Enter The Same New Password", Toast.LENGTH_LONG).show()
                }
                else if(newpsw.length < 6){
                    Toast.makeText(requireContext(), "Please Enter At Least 6 Characters For Password", Toast.LENGTH_LONG).show()
                }
                else if(oldpsw != sharedPreferences.getString("password","")){
                    Toast.makeText(requireContext(), "Enter Your Old Password Correctly", Toast.LENGTH_LONG).show()
                }
                else{
                    lifecycleScope.launch {

                        val result = async {
                            supabase.gotrue.modifyUser(true) {
                                password = newpsw
                            }
                            val editor: SharedPreferences.Editor = sharedPreferences.edit()
                            editor.putString("password", newpsw)
                            editor.apply()
                        }
                        val asyncResult = result.await()
                        Toast.makeText(requireContext(),"Change Password Successful", Toast.LENGTH_LONG).show()
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
            }
            else{
                Toast.makeText(requireContext(),"Please Fill All Fields", Toast.LENGTH_LONG).show()
            }

        }

        return binding.root
    }


}
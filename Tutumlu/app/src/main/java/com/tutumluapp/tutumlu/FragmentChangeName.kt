package com.tutumluapp.tutumlu

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.tutumluapp.tutumlu.databinding.FragmentChangeNameBinding
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserInfo
import io.github.jan.supabase.gotrue.user.UserUpdateBuilder
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.FilterOperation
import io.github.jan.supabase.postgrest.query.FilterOperator
import io.github.jan.supabase.postgrest.query.PostgrestUpdate
import io.github.jan.supabase.postgrest.request.PostgrestRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*
import org.json.JSONObject
import org.slf4j.MDC.put

class FragmentChangeName : Fragment() {

    private lateinit var binding:FragmentChangeNameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentChangeNameBinding.inflate(inflater,container,false)

        val supabaseUrl = "https://qbpruczdytiwqouzoztl.supabase.co"
        val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFicHJ1Y3pkeXRpd3FvdXpvenRsIiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTcxMzE0ODUsImV4cCI6MjAxMjcwNzQ4NX0.aWqvr3-qOnJGQXZwk4GVNVXRnd2OKMOn3fkWF8Pm2UE"

        val supabase = createSupabaseClient(supabaseUrl, supabaseKey) {
            install(GoTrue)
            install(Postgrest)
        }

        lifecycleScope.launch {

            val result = async {

            }

            val asyncResult = result.await()
        }


        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("Credentials", Context.MODE_PRIVATE)
        val profile_name = sharedPreferences.getString("profileName", "")
        val user_name = "@" + sharedPreferences.getString("userName", "")

        binding.lblProfilename.text = profile_name
        binding.lblUsername.text = user_name

        binding.hdrChangename.ttHdr.text = "Change Name"
        binding.hdrChangename.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnChangename.setOnClickListener {
            val new_name = binding.inputUsername.text.toString()
            val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("Credentials", Context.MODE_PRIVATE)


            if(!new_name.isNullOrEmpty()){
                lifecycleScope.launch {

                    val result = async {
                        supabase.gotrue.modifyUser(true) {
                            data = buildJsonObject {
                                put("profile_name", new_name)
                            }
                        }
                        supabase.postgrest.from("users").update( buildJsonObject {
                            put("profile_name", new_name) }
                        )
                        {
                            sharedPreferences.getString("userName", "")
                                ?.let { it1 -> FilterOperation("user_name",FilterOperator.EQ, it1) }
                                ?.let { it2 -> filter(it2) }
                        }
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()

                        editor.putString("profileName", new_name.toString()?.replace("\"",""))
                        editor.apply()
                    }
                    val asyncResult = result.await()
                    Toast.makeText(requireContext(),"Change Profile Name Successful", Toast.LENGTH_LONG).show()
                    requireActivity().supportFragmentManager.popBackStack()
                }

                }
            else{
                Toast.makeText(requireContext(),"Please Fill All Fields", Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }
}
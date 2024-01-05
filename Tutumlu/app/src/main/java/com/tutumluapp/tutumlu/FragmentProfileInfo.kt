package com.tutumluapp.tutumlu

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.tutumluapp.tutumlu.databinding.FragmentProfileInfoBinding
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class FragmentProfileInfo : Fragment() {

    private lateinit var binding: FragmentProfileInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileInfoBinding.inflate(inflater,container,false)

        val supabaseUrl = "https://qbpruczdytiwqouzoztl.supabase.co"
        val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFicHJ1Y3pkeXRpd3FvdXpvenRsIiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTcxMzE0ODUsImV4cCI6MjAxMjcwNzQ4NX0.aWqvr3-qOnJGQXZwk4GVNVXRnd2OKMOn3fkWF8Pm2UE"

        val supabase = createSupabaseClient(supabaseUrl, supabaseKey) {
            install(GoTrue)
            install(Postgrest)
        }

        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("Credentials", Context.MODE_PRIVATE)
        val profile_name = sharedPreferences.getString("profileName", "")
        val user_name = "@" + sharedPreferences.getString("userName", "")

        binding.lblProfilename.text = profile_name
        binding.lblUsername.text = user_name

        binding.hdrProfile.ttHdr.text = "Profile"
        binding.hdrProfile.btnOptional.setImageResource(R.drawable.btn_exit)
        binding.hdrProfile.btnOptional.setOnClickListener {
            lifecycleScope.launch {

                val result = async {

                    supabase.gotrue.logout()

                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.clear()
                    editor.commit()
                    requireActivity().finishAffinity()
                }
                val asyncResult = result.await()
            }
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
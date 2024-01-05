package com.tutumluapp.tutumlu

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.tutumluapp.tutumlu.databinding.ActivityLogInBinding
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserInfo
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*

class LogInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val supabaseUrl = "https://qbpruczdytiwqouzoztl.supabase.co"
        val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFicHJ1Y3pkeXRpd3FvdXpvenRsIiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTcxMzE0ODUsImV4cCI6MjAxMjcwNzQ4NX0.aWqvr3-qOnJGQXZwk4GVNVXRnd2OKMOn3fkWF8Pm2UE"

        val supabase = createSupabaseClient(supabaseUrl, supabaseKey) {
            install(GoTrue)
            install(Postgrest)
        }


        binding = ActivityLogInBinding.inflate(layoutInflater)

        binding.hdrLogin.ttHdr.text = "Login"
        binding.hdrLogin.btnBack.setOnClickListener {
            finish()
        }

        binding.btnLogin.setOnClickListener {
            if(binding.inputUsername.text.toString().isNotEmpty() && binding.inputPassword.text.toString().isNotEmpty())
            {
                val emailUser = binding.inputUsername.text.toString()
                val psw = binding.inputPassword.text.toString()
                if(psw.length < 6){
                    Toast.makeText(this, "Please Enter At Least 6 Characters For Password", Toast.LENGTH_LONG).show()
                }
                else{
                    lifecycleScope.launch {

                        val result = async {
                            try {
                                supabase.gotrue.loginWith(Email) {
                                    email = emailUser
                                    password = psw
                                }
                                val userData : UserInfo= supabase.gotrue.retrieveUserForCurrentSession()
                                val profile_name = userData?.userMetadata?.get("profile_name")
                                val user_name = userData?.userMetadata?.get("user_name")

                                val sharedPreferences: SharedPreferences = getSharedPreferences("Credentials", Context.MODE_PRIVATE)
                                val editor: SharedPreferences.Editor = sharedPreferences.edit()

                                editor.putString("profileName", profile_name.toString())
                                editor.putString("userName", user_name.toString())
                                editor.apply()

                                startActivity(Intent(applicationContext, HomeActivity::class.java))

                            }
                            catch(e : java.lang.Exception){
                                Toast.makeText(applicationContext, "Invalid Credentials", Toast.LENGTH_LONG).show()
                            }
                        }

                        val asyncResult = result.await()
                    }

                }

            }
            else{
                Toast.makeText(this, "Please Fill All Fields", Toast.LENGTH_LONG).show()
            }

        }

        setContentView(binding.root)
    }
}
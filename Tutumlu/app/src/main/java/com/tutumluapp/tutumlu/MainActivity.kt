package com.tutumluapp.tutumlu

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.tutumluapp.tutumlu.databinding.ActivityMainBinding
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val supabaseUrl = "https://qbpruczdytiwqouzoztl.supabase.co"
        val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFicHJ1Y3pkeXRpd3FvdXpvenRsIiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTcxMzE0ODUsImV4cCI6MjAxMjcwNzQ4NX0.aWqvr3-qOnJGQXZwk4GVNVXRnd2OKMOn3fkWF8Pm2UE"

        val supabase = createSupabaseClient(supabaseUrl, supabaseKey) {
            install(GoTrue)
            install(Postgrest)
        }

        lifecycleScope.launch {

            val result = async {
                if(supabase.gotrue.currentUserOrNull() == null){
                    val sharedPreferences: SharedPreferences = getSharedPreferences("Credentials", Context.MODE_PRIVATE)
                    val emailUser = sharedPreferences.getString("email", "")
                    val psw = sharedPreferences.getString("password", "")
                    try {
                        if(!emailUser.isNullOrEmpty() && !psw.isNullOrEmpty()) {
                            supabase.gotrue.loginWith(Email) {
                                email = emailUser
                                password = psw
                            }
                        }
                        else{
                            binding.btnSignup.setOnClickListener {
                                startActivity(Intent(applicationContext, SignUpActivity::class.java))
                            }

                            binding.btnLogin.setOnClickListener {
                                startActivity(Intent(applicationContext, LogInActivity::class.java))
                            }

                            setContentView(binding.root)
                        }
                    }
                    catch(e : Exception){
                        binding.btnSignup.setOnClickListener {
                            startActivity(Intent(applicationContext, SignUpActivity::class.java))
                        }

                        binding.btnLogin.setOnClickListener {
                            startActivity(Intent(applicationContext, LogInActivity::class.java))
                        }

                        setContentView(binding.root)
                    }
                }
                else{
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                }

            }

            val asyncResult = result.await()
        }
    }
}


package com.tutumluapp.tutumlu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.tutumluapp.tutumlu.databinding.ActivitySignUpBinding
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.security.auth.login.LoginException

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val supabaseUrl = "https://qbpruczdytiwqouzoztl.supabase.co"
        val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFicHJ1Y3pkeXRpd3FvdXpvenRsIiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTcxMzE0ODUsImV4cCI6MjAxMjcwNzQ4NX0.aWqvr3-qOnJGQXZwk4GVNVXRnd2OKMOn3fkWF8Pm2UE"

        val supabase = createSupabaseClient(supabaseUrl, supabaseKey) {
            install(GoTrue)
            install(Postgrest)
        }


        binding = ActivitySignUpBinding.inflate(layoutInflater)

        binding.hdrSignup.ttHdr.text = "Sign Up"
        binding.hdrSignup.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSignup.setOnClickListener {
            if(binding.inputEmail.text.toString().isNotEmpty() && binding.inputUsername.text.toString().isNotEmpty() && binding.inputPassword.text.toString().isNotEmpty() && binding.inputPasswordAgain.text.toString().isNotEmpty() && binding.inputProfilename.text.toString().isNotEmpty())
            {
                val emailUser = binding.inputEmail.text.toString()
                val userName = binding.inputUsername.text.toString()
                val profileName = binding.inputProfilename.text.toString()
                val psw = binding.inputPassword.text.toString()
                val pswAgain = binding.inputPasswordAgain.text.toString()
                if(psw != pswAgain){
                    Toast.makeText(this, "Please Enter The Same Password", Toast.LENGTH_LONG).show()
                }
                else if(psw.length < 6){
                    Toast.makeText(this, "Please Enter At Least 6 Characters For Password", Toast.LENGTH_LONG).show()
                }
                else{
                    lifecycleScope.launch {

                        val result = async {
                            supabase.gotrue.signUpWith(Email){
                                email = emailUser
                                password = psw
                                data = buildJsonObject {
                                    put("user_name", userName)
                                    put("profile_name", profileName)
                                }
                            }
                        }

                        val asyncResult = result.await()
                        Toast.makeText(applicationContext, "Please Check Your Mail", Toast.LENGTH_LONG).show()
                        startActivity(Intent(applicationContext, LogInActivity::class.java))
                    }

                }

            }
            else{
                Toast.makeText(this, "Please Fill All Fields", Toast.LENGTH_LONG).show()
            }

        //startActivity(Intent(this, HomeActivity::class.java))
        }

        setContentView(binding.root)
    }
}
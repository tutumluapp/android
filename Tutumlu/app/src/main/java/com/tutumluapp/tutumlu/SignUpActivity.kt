package com.tutumluapp.tutumlu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tutumluapp.tutumlu.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)

        binding.hdrSignup.ttHdr.text = "Sign Up"
        binding.hdrSignup.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSignup.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        setContentView(binding.root)
    }
}
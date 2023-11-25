package com.tutumluapp.tutumlu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tutumluapp.tutumlu.databinding.ActivityLogInBinding

class LogInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)

        binding.hdrLogin.ttHdr.text = "Login"
        binding.hdrLogin.btnBack.setOnClickListener {
            finish()
        }

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        setContentView(binding.root)
    }
}
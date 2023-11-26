package com.tutumluapp.tutumlu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tutumluapp.tutumlu.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding:ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)



        setContentView(binding.root)

        val myProfileFragment = FragmentProfileInfo()

        // Add the fragment to the container using the supportFragmentManager
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_profile, myProfileFragment)
            .commit()
    }
}
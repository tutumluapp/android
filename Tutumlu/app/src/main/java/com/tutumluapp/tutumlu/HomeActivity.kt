package com.tutumluapp.tutumlu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import com.tutumluapp.tutumlu.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding:ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)

        binding.hdrHome.ttHdr.text = "Home"
        binding.hdrHome.btnOptional.setImageResource(R.drawable.btn_profile)

        binding.btnSearch.btnHdr.text = "Search Item"
        binding.btnSearch.btnOptional.setImageResource(R.drawable.logo_scan)

        binding.btnUpload.btnHdr.text = "Upload Slip"
        binding.btnUpload.btnOptional.setImageResource(R.drawable.logo_scan)


        setContentView(binding.root)
    }
}
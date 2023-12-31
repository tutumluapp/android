package com.tutumluapp.tutumlu

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tutumluapp.tutumlu.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {

    private lateinit var binding:ActivityHomeBinding
    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)

        binding.hdrHome.ttHdr.text = "Home"
        binding.hdrHome.btnOptional.setImageResource(R.drawable.btn_profile)
        binding.hdrHome.btnOptional.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.btnSearch.btnHdr.text = "Search Item"
        binding.btnSearch.btnOptional.setImageResource(R.drawable.logo_search)
        binding.btnSearch.clickableLayout.setOnClickListener {
            // Check for camera permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE)
            } else {
                startActivity(Intent(this, BarcodeScannerActivity::class.java))
            }

            //startActivity(Intent(this, SearchActivity::class.java))
        }

        binding.btnUpload.btnHdr.text = "Upload Slip"
        binding.btnUpload.btnOptional.setImageResource(R.drawable.logo_scan)
        binding.btnUpload.clickableLayout.setOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }

        setContentView(binding.root)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(Intent(this, BarcodeScannerActivity::class.java))
                // Permission granted, proceed with camera operations
                // Initialize and use the camera here
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
            }
        }
    }

}
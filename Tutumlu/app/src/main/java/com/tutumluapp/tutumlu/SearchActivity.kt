package com.tutumluapp.tutumlu

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tutumluapp.tutumlu.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)

        val fragmentSearchBarcode = FragmentSearchBarcode()
        val fragmentSearchName = FragmentSearchName()

        binding.hdrSearch.ttHdr.text = "Search"
        binding.hdrSearch.btnOptional.setImageResource(R.drawable.btn_profile)
        binding.hdrSearch.btnOptional.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        binding.hdrSearch.btnBack.setOnClickListener {
            finish()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_search, fragmentSearchName)
            .addToBackStack(null)
            .commit()

        binding.btnSearchName.setOnClickListener {
            binding.btnSearchName.background = resources.getDrawable(R.drawable.background_clicked)
            binding.btnSearchName.setTextColor(Color.WHITE)

            binding.btnSearchBarcode.background = resources.getDrawable(R.drawable.background_not_clicked)
            binding.btnSearchBarcode.setTextColor(Color.parseColor("#828282"))

            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_search, fragmentSearchName)
                .addToBackStack(null)
                .commit()
        }

        binding.btnSearchBarcode.setOnClickListener {
            binding.btnSearchName.background = resources.getDrawable(R.drawable.background_not_clicked)
            binding.btnSearchName.setTextColor(Color.parseColor("#828282"))

            binding.btnSearchBarcode.background = resources.getDrawable(R.drawable.background_clicked)
            binding.btnSearchBarcode.setTextColor(Color.WHITE)

            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_search, fragmentSearchBarcode)
                .addToBackStack(null)
                .commit()
        }


        setContentView(binding.root)
    }
}
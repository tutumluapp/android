package com.tutumluapp.tutumlu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tutumluapp.tutumlu.databinding.ActivityUploadBinding
import com.tutumluapp.tutumlu.databinding.ProductUploadFieldBinding

class UploadActivity : AppCompatActivity() {
    private lateinit var binding:ActivityUploadBinding
    private class Product(nameInput: String, barcodeInput: String, priceInput:Double, status: Int){
        val name : String = nameInput
        var barcode : String = barcodeInput
        val price : Double = priceInput
        var scanStatus : Int = status
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)

        binding.hdrUpload.ttHdr.text = "Upload Slip"
        binding.hdrUpload.btnBack.setOnClickListener {
            finish()
        }
        binding.hdrUpload.btnOptional.setImageResource(R.drawable.btn_profile)
        binding.hdrUpload.btnOptional.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        val customItems = arrayOf(
            Product("Item 1", "", 2.5, 1),
            Product("Item 2", "", 2.5, 1),
            Product("Item 3", "", 2.5, 1)
        )

        customItems.forEach {product ->
            binding.llProduct.addView(
                ProductUploadFieldBinding.inflate(layoutInflater).also {listItem ->
                    listItem.lblItemName.text = product.name
                    listItem.lblScanInfo.text = "Scan barcode"
                    listItem.lblPrice.text = product.price.toString()
                    listItem.btnScan.setImageResource(R.drawable.logo_scan_black)
                    listItem.btnScan.setOnClickListener {
                        listItem.lblScanInfo.text = "Barcode canned!"
                        listItem.btnScan.setImageResource(R.drawable.logo_tick)
                        product.scanStatus = 0
                    }
                }.root
            )
        }


        setContentView(binding.root)
    }
}
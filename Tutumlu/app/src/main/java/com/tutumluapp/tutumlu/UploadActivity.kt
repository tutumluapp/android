package com.tutumluapp.tutumlu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.tutumluapp.tutumlu.databinding.ActivityUploadBinding
import com.tutumluapp.tutumlu.databinding.ProductUploadFieldBinding

class UploadActivity : AppCompatActivity() {
    private lateinit var binding:ActivityUploadBinding
    class Product(nameInput: String, barcodeInput: String, priceInput:Double, status: Int){
        val name : String = nameInput
        var barcode : String = barcodeInput
        val price : Double = priceInput
        var scanStatus : Int = status
    }

    var customerItems = arrayOf(
        Product("Item 1", "", 2.5, 1),
        Product("Item 2", "", 2.5, 1),
        Product("Item 3", "", 2.5, 1)
    )

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



        customerItems.forEach {product ->
            val listItemBinding = ProductUploadFieldBinding.inflate(layoutInflater)
            val listItemView = listItemBinding.root

            // Set the tag to the product name
            listItemView.tag = product.name

            // Update attributes
            listItemBinding.lblItemName.text = product.name
            listItemBinding.lblScanInfo.text = "Scan barcode"
            listItemBinding.lblPrice.text = product.price.toString()
            listItemBinding.btnScan.setImageResource(R.drawable.logo_scan_black)

            listItemBinding.btnScan.setOnClickListener {
                val intent = Intent(this, BarcodeScannerActivity::class.java)
                intent.putExtra("product", product.name)

                // Start the called activity
                startActivityForResult(intent, 123)

            }

            // Add the view to the linear layout
            binding.llProduct.addView(listItemView)

            /*binding.llProduct.addView(
                ProductUploadFieldBinding.inflate(layoutInflater).also {listItem ->

                    listItem.lblItemName.text = product.name
                    listItem.lblScanInfo.text = "Scan barcode"
                    listItem.lblPrice.text = product.price.toString()
                    listItem.btnScan.setImageResource(R.drawable.logo_scan_black)
                    listItem.btnScan.setOnClickListener {
                        val intent = Intent(this, BarcodeScannerActivity::class.java)

                        // Pass the callback and item to the called activity
                        intent.putExtra("callback", this)
                        intent.putExtra("product", product.name)

                        // Start the called activity
                        startActivity(intent)


                        listItem.lblScanInfo.text = "Barcode scanned!"
                        //listItem.btnScan.setImageResource(R.drawable.logo_tick)
                    }
                }.root
            )*/
        }


        setContentView(binding.root)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 123 && resultCode == RESULT_OK){
            val product = data?.getStringExtra("product")
            val barcode = data?.getStringExtra("barcode")

            val scannedProduct = customerItems.find { product == it.name }
            if (barcode != null) {
                scannedProduct?.barcode = barcode
                scannedProduct?.scanStatus = 0

                for(i in 0 until binding.llProduct.childCount){
                    val view: View = binding.llProduct.getChildAt(i)
                    if (view.tag == scannedProduct?.name) {
                        val listItemBinding = ProductUploadFieldBinding.bind(view)

                        listItemBinding.lblScanInfo.text = "Barcode Scanned!"
                        listItemBinding.btnScan.setImageResource(R.drawable.logo_tick)
                    }
                }
            }
        }
    }
}
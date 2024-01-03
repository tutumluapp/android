package com.tutumluapp.tutumlu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

    /*var customerItems = arrayOf(
        Product("Item 1", "", 2.5, 1),
        Product("Item 2", "", 2.5, 1),
        Product("Item 3", "", 2.5, 1)
    )*/
    var customerItems = ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val receiptText = intent.getStringExtra("receipt")

        val indexOfDate = receiptText?.indexOf("TARIH:")
        val indexOfHour = receiptText?.indexOf("SAAT:")
        if(indexOfDate != null && indexOfHour != null){
            val indexOfNewLineAfterDate = receiptText?.indexOf("\n", indexOfDate)
            val indexOfNewLineAfterHour = receiptText.indexOf("\n", indexOfHour)
            if(indexOfNewLineAfterDate != null && indexOfNewLineAfterHour != null){
                val date = receiptText.substring(indexOfDate + 6, indexOfNewLineAfterDate).replace(".", "/").replace(" ", "").trim()
                val hour = receiptText.substring(indexOfHour + 5, indexOfNewLineAfterHour).replace(" ", "").trim()
                binding.marketNameInfo.lblDate.text = "DATE: " + date
                binding.marketNameInfo.lblTime.text = "TIME: " + hour
            }

        }

        val lines = receiptText?.split("\n")

        var strToCompare = ""
        if(lines?.indexOfFirst { it.contains("FIŞ NO") } != -1){
            strToCompare = "FIŞ NO"
        }
        else if(lines?.indexOfFirst { it.contains("FİŞ NO") } != -1){
            strToCompare = "FİŞ NO"
        }
        else if(lines?.indexOfFirst { it.contains("FİS NO") } != -1){
            strToCompare = "FİS NO"
        }
        else if(lines?.indexOfFirst { it.contains("FIS NO") } != -1){
            strToCompare = "FIS NO"
        }
        if(strToCompare != ""){
            val fisNoLineNumber = lines?.indexOfFirst { it.contains(strToCompare) }?.plus(2)

            val topkdvLineNumber = lines?.indexOfFirst { it.contains("TOPKDV") }

            if(fisNoLineNumber != null && topkdvLineNumber != null){
                val fisNoToTopkdvText = lines?.subList(fisNoLineNumber, topkdvLineNumber)?.joinToString("\n")

                var prices = ArrayList<Double>()
                var items = ArrayList<String>()

                fisNoToTopkdvText?.split("\n")?.forEach { line ->
                    when {
                        (line.startsWith("*") || line.startsWith("x")) && (!line.startsWith("**") && !line.startsWith("x*") && !line.startsWith("X*") && !line.startsWith("*X")) -> {
                            val price = line.drop(1).trim().replace(",", ".").replace(" ", "").toDouble()
                            prices.add(price)
                        }
                        line.startsWith("%") -> {
                            // Handle percentage if needed
                        }
                        else -> {
                            if (!line.first().isDigit() && !line.contains("ARA TOPLAM")) {
                                items.add(line.trim())
                            }
                        }
                    }
                }

                var i = 0
                items.forEach {item ->
                    customerItems.add(Product(item, "", prices[i], 1))
                    if(i < prices.size - 1){
                        i = i + 1
                    }
                }
            }
        }

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

            listItemView.tag = product.name

            listItemBinding.lblItemName.text = product.name
            listItemBinding.lblScanInfo.text = "Scan barcode"
            listItemBinding.lblPrice.text = product.price.toString()
            listItemBinding.btnScan.setImageResource(R.drawable.logo_scan_black)

            listItemBinding.btnScan.setOnClickListener {
                val intent = Intent(this, BarcodeScannerActivity::class.java)
                intent.putExtra("product", product.name)

                startActivityForResult(intent, 123)

            }

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
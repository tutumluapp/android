package com.tutumluapp.tutumlu

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tutumluapp.tutumlu.databinding.ActivityUploadBinding
import com.tutumluapp.tutumlu.databinding.ProductUploadFieldBinding
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.PostgrestResult
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull


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

        val spinner = binding.marketNameInfo.spinner1

        val res: Resources = resources
        val optionsArray: Array<String> = res.getStringArray(R.array.options_array)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, optionsArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter


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
            val fisNoLineNumber = lines?.indexOfFirst { it.contains(strToCompare) }?.plus(1)

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

        binding.btnUpload.root.setOnClickListener {
             if( customerItems.any { it.scanStatus == 1 }){
                 Toast.makeText(this, "Please scan all items", Toast.LENGTH_SHORT).show()
             }
            else{
                 val supabaseUrl = "https://qbpruczdytiwqouzoztl.supabase.co"
                 val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFicHJ1Y3pkeXRpd3FvdXpvenRsIiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTcxMzE0ODUsImV4cCI6MjAxMjcwNzQ4NX0.aWqvr3-qOnJGQXZwk4GVNVXRnd2OKMOn3fkWF8Pm2UE"

                 val supabase = createSupabaseClient(supabaseUrl, supabaseKey) {
                     install(GoTrue)
                     install(Postgrest)
                 }

                 val prices = supabase.postgrest.from("prices")
                 val products = supabase.postgrest.from("products")
                 val scans = supabase.postgrest.from("scans")
                 val markets = supabase.postgrest.from("markets")

                 @Serializable
                 data class Scan(
                     @SerialName("market_id") val market_id: Int
                 )
                 @Serializable
                 data class Price(
                     @SerialName("product_id") val barcode: String,
                     @SerialName("scan_id") val scan: Long?,
                     @SerialName("price") val price: Double
                 )

                 lifecycleScope.launch {

                     val result = async {
                         var marketid = 1
                         if(binding.marketNameInfo.spinner1.selectedItem == "A101"){
                             marketid = 1
                         }
                         else if(binding.marketNameInfo.spinner1.selectedItem == "ŞOK"){
                             marketid = 2
                         }
                         else{
                             marketid = 3
                         }
                         var i : PostgrestResult = scans.insert(Scan(marketid))
                         var scanQuery : PostgrestResult = scans.select(columns = Columns.list("id")) {
                             order("id", Order.DESCENDING)
                             limit(1)
                         }
                         var scanList =
                             scanQuery.body?.jsonArray?.map { it.jsonObject["id"]?.jsonPrimitive?.longOrNull }
                         val maxScanNum = scanList?.get(0)

                         Log.i("TEST", maxScanNum.toString())

                         customerItems.forEach{item ->
                             if (maxScanNum != null) {
                                 prices.insert(Price(item.barcode, maxScanNum, item.price))
                             }
                         }
                         delay(2000)
                     }
                     val asyncResult = result.await()
                     finish()
                 }
             }
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
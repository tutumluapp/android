package com.tutumluapp.tutumlu

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.tutumluapp.tutumlu.databinding.FragmentSearchBarcodeBinding
import com.tutumluapp.tutumlu.databinding.LlBarcodeItemListBinding
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.FilterOperation
import io.github.jan.supabase.postgrest.query.FilterOperator
import io.github.jan.supabase.postgrest.query.PostgrestResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*


class FragmentSearchBarcode : Fragment() {
    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    class ProductSearch(marketInput: String, priceInput: String){
        var market : String = marketInput
        val price : String = priceInput
    }

    private lateinit var binding:FragmentSearchBarcodeBinding

    val searchItems = ArrayList<ProductSearch>(
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBarcodeBinding.inflate(inflater,container,false)

        binding.scanStatusLogo.setImageResource(R.drawable.logo_cross)
        binding.scanStatusLbl.text = "No barcode scanned"

        binding.btnScanBarcode.btnHdr.text = "SCAN BARCODE"
        binding.btnScanBarcode.btnOptional.setImageResource(R.drawable.logo_scan)

        binding.btnScanBarcode.clickableLayout.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE)
            } else {
                val intent = Intent(requireContext(), BarcodeScannerActivity::class.java)
                intent.putExtra("product", "")

                startActivityForResult(intent, 123)
            }
        }

        return binding?.root
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(requireContext(), BarcodeScannerActivity::class.java)
                intent.putExtra("product", "")

                startActivityForResult(intent, 123)
                //startActivity(Intent(this, BarcodeScannerActivity::class.java))
            } else {
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 123 && resultCode == AppCompatActivity.RESULT_OK){
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

            var barcode = data?.getStringExtra("barcode")

            lifecycleScope.launch {

                val result = async {
                    Log.i("Barkod", barcode.toString())

                var queryResult : PostgrestResult = products.select(columns = Columns.list("gtin, name")) {
                    barcode?.let { FilterOperation("gtin", FilterOperator.EQ, it.toString()) }
                        ?.let {op -> filter(op) }
                }
                var barcodes = queryResult.body?.jsonArray?.map { it.jsonObject["gtin"]?.jsonPrimitive?.longOrNull }
                var names = queryResult.body?.jsonArray?.map { it.jsonObject["name"]?.jsonPrimitive?.contentOrNull }


                var productPriceQuery: PostgrestResult =
                        prices.select(columns = Columns.list("price, scan_id, product_id")) {
                            filter(
                                FilterOperation(
                                    "product_id",
                                    FilterOperator.EQ,
                                    barcode.toString()
                                )
                            )
                        }
                    var product: FragmentSearchName.Product? = null
                    var scanList =
                        productPriceQuery.body?.jsonArray?.map { it.jsonObject["scan_id"]?.jsonPrimitive?.longOrNull }
                    var priceList =
                        productPriceQuery.body?.jsonArray?.map { it.jsonObject["price"]?.jsonPrimitive?.doubleOrNull }

                    scanList?.forEach { scanID ->

                        var marketQueryResult: PostgrestResult = scans.select {
                            filter(
                                FilterOperation(
                                    "market_id",
                                    FilterOperator.EQ, scanID.toString()
                                )
                            )
                        }
                        val marketId =
                            marketQueryResult.body?.jsonArray?.map { it.jsonObject["market_id"]?.jsonPrimitive?.intOrNull }
                        Log.i("TEST", marketId.toString())
                        var strMarket = ""
                        if(marketId.toString().contains("1")){
                            strMarket = "A101"
                        }
                        else if(marketId.toString().contains("2")){
                            strMarket = "ŞOK"
                        }
                        else{
                            strMarket = "MİGROS"
                        }
                        searchItems.add(ProductSearch(strMarket, priceList?.get(scanList.indexOf(scanID)).toString()))
                    }
                    binding.scanStatusLogo.setImageResource(R.drawable.logo_tick)
                    binding.scanStatusLbl.text = "Barcode scanned!"

                    binding.lblItemName.text = names.toString().replace("[","").replace("]","")

                    searchItems.forEach { product ->
                        binding.llPriceListBarcode.addView(
                            LlBarcodeItemListBinding.inflate(layoutInflater).also { listItem ->
                                listItem.listItemMarket.text = "Market: " + product.market
                                listItem.listItemPrice.text = "Price: " + product.price.toString()
                            }.root
                        )
                    }

                }
                delay(2000)
                val asyncResult = result.await()

            }
        }
    }
}
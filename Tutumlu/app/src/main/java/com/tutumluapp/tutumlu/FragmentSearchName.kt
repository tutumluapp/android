package com.tutumluapp.tutumlu

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.tutumluapp.tutumlu.databinding.FieldSearchItemBinding
import com.tutumluapp.tutumlu.databinding.FragmentSearchNameBinding
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


class FragmentSearchName : Fragment() {
    private lateinit var binding:FragmentSearchNameBinding
    public class Product(nameInput:String, markets: Array<Market?>){
        val name:String = nameInput
        val marketList: Array<Market?> = markets
    }
    class Market(marketInput: String, priceInput:Double){
        val market : String = marketInput
        val price : Double = priceInput
    }

    var searchMarkets = ArrayList<Market>(
        //Market("A101", 2.5),
        //Market("ŞOK", 3.5),
        //Market("BİM", 4.0)
    )

    var searchItems = ArrayList<Product>(
        //Product("Item 1", searchMarkets),
        //Product("Item 2", searchMarkets),
        //Product("Item 3", searchMarkets)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchNameBinding.inflate(inflater,container,false)


        binding.searchItemInput.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(v.text)
                return@setOnEditorActionListener true
            }
            false
        }

        return binding?.root
    }

    private fun performSearch(v:CharSequence) {
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


        lifecycleScope.launch {

            val result = async {
                var queryResult : PostgrestResult = products.select(columns = Columns.list("gtin, name")) {
                    filter(FilterOperation("name", FilterOperator.LIKE,"%${v.toString()}%") )
                }
                var barcodes = queryResult.body?.jsonArray?.map { it.jsonObject["gtin"]?.jsonPrimitive?.longOrNull }
                var names = queryResult.body?.jsonArray?.map { it.jsonObject["name"]?.jsonPrimitive?.contentOrNull }
                Log.i("Algida", barcodes.toString())

                barcodes?.forEach { barcode->
                    Log.i("Barkod", barcode.toString())
                    var productPriceQuery : PostgrestResult = prices.select(columns = Columns.list("price, scan_id, product_id")){
                        filter(FilterOperation("product_id", FilterOperator.EQ, barcode.toString()))
                    }
                    var product : Product? = null
                    var scanList = productPriceQuery.body?.jsonArray?.map { it.jsonObject["scan_id"]?.jsonPrimitive?.longOrNull }
                    var priceList = productPriceQuery.body?.jsonArray?.map { it.jsonObject["price"]?.jsonPrimitive?.doubleOrNull }
                    val name = names?.get(barcodes.indexOf(barcode))
                    if(name != null && priceList?.isNotEmpty() == true){
                        product = Product(name,arrayOfNulls<Market>(priceList.size))
                    }

                    scanList?.forEach { scanID ->
                        var firstNullIndex = product?.marketList?.indexOfFirst { it == null }

                        var marketQueryResult : PostgrestResult = scans.select {
                            filter(FilterOperation("market_id",FilterOperator.EQ, scanID.toString()))
                        }
                        val marketId = marketQueryResult.body?.jsonArray?.map { it.jsonObject["market_id"]?.jsonPrimitive?.longOrNull }
                        if (firstNullIndex != null) {
                            priceList?.get(scanList.indexOf(scanID))
                                ?.let { pr -> Market(marketId.toString(), pr) }
                                ?.let { product?.marketList?.set(firstNullIndex, it) }
                        }
                        if (product != null) {
                            searchItems.add(product)
                        }

                    }

                }
                delay(2000)
            }

            val asyncResult = result.await()

            searchItems.distinct().forEach {product ->
                binding.llItemListName.addView(
                    FieldSearchItemBinding.inflate(layoutInflater).also { listItem ->
                        listItem.lblItem.text = product.name
                        listItem.btnDown.setOnClickListener {
                            listItem.scrollViewSearch.visibility = View.VISIBLE
                            listItem.llHide.visibility = View.VISIBLE
                            listItem.llShow.visibility = View.GONE
                        }
                        listItem.btnUp.setOnClickListener {
                            listItem.scrollViewSearch.visibility = View.GONE
                            listItem.llHide.visibility = View.GONE
                            listItem.llShow.visibility = View.VISIBLE
                        }
                        product.marketList.distinct().forEach {market ->
                            listItem.llPriceListSearch.addView(
                                LlBarcodeItemListBinding.inflate(layoutInflater).also { listItem ->
                                    if(market?.market?.contains("1") == true){
                                        listItem.listItemMarket.text = "Market: " + "A101"
                                    }
                                    else if(market?.market?.contains("2") == true){
                                        listItem.listItemMarket.text = "Market: " + "ŞOK"
                                    }
                                    else{
                                        listItem.listItemMarket.text = "Market: " + "MİGROS"
                                    }
                                    listItem.listItemPrice.text = "Price: " + market?.price.toString()
                                }.root
                            )
                        }
                    }.root
                )
            }

            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)

        }
    }
}
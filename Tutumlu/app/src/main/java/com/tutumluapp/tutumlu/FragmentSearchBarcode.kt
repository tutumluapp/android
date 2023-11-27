package com.tutumluapp.tutumlu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tutumluapp.tutumlu.databinding.FragmentSearchBarcodeBinding
import com.tutumluapp.tutumlu.databinding.LlBarcodeItemListBinding
import com.tutumluapp.tutumlu.databinding.ProductUploadFieldBinding


class FragmentSearchBarcode : Fragment() {

    private class Product(marketInput: String, priceInput:Double){
        var market : String = marketInput
        val price : Double = priceInput
    }

    private lateinit var binding:FragmentSearchBarcodeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBarcodeBinding.inflate(inflater,container,false)

        binding.scanStatusLogo.setImageResource(R.drawable.logo_cross)
        binding.scanStatusLbl.text = "No barcode scanned"

        binding.btnScanBarcode.btnHdr.text = "SCAN BARCODE"
        binding.btnScanBarcode.btnOptional.setImageResource(R.drawable.logo_scan)

        val searchItems = arrayOf(
            Product("A101", 2.5),
            Product("ŞOK",  3.5),
            Product("BİM",  4.0)
        )

        binding.btnScanBarcode.clickableLayout.setOnClickListener {
            binding.scanStatusLogo.setImageResource(R.drawable.logo_tick)
            binding.scanStatusLbl.text = "Barcode scanned!"

            binding.lblItemName.text = "Item 2's Name"

            searchItems.forEach {product ->
                binding.llPriceListBarcode.addView(
                    LlBarcodeItemListBinding.inflate(layoutInflater).also { listItem ->
                        listItem.listItemMarket.text = "Market: " + product.market
                        listItem.listItemPrice.text = "Price: $" + product.price.toString()
                    }.root
                )
            }
        }



        return binding?.root
    }


}
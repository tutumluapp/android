package com.tutumluapp.tutumlu

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.tutumluapp.tutumlu.databinding.FieldSearchItemBinding
import com.tutumluapp.tutumlu.databinding.FragmentSearchNameBinding
import com.tutumluapp.tutumlu.databinding.LlBarcodeItemListBinding


class FragmentSearchName : Fragment() {
    private lateinit var binding:FragmentSearchNameBinding
    public class Product(nameInput:String, markets:Array<Market>){
        val name:String = nameInput
        val marketList:Array<Market> = markets
    }
    class Market(marketInput: String, priceInput:Double){
        val market : String = marketInput
        val price : Double = priceInput
    }

    val searchMarkets = arrayOf(
        Market("A101", 2.5),
        Market("ŞOK", 3.5),
        Market("BİM", 4.0)
    )

    val searchItems = arrayOf(
        Product("Item 1", searchMarkets),
        Product("Item 2", searchMarkets),
        Product("Item 3", searchMarkets)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchNameBinding.inflate(inflater,container,false)


        binding.searchItemInput.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                return@setOnEditorActionListener true
            }
            false
        }



        return binding?.root
    }

    private fun performSearch() {
        searchItems.forEach {product ->
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
                    product.marketList.forEach {market ->
                        listItem.llPriceListSearch.addView(
                            LlBarcodeItemListBinding.inflate(layoutInflater).also { listItem ->
                                listItem.listItemMarket.text = "Market: " + market.market
                                listItem.listItemPrice.text = "Price: $" + market.price.toString()
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
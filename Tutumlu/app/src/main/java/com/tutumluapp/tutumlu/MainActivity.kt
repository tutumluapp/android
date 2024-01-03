package com.tutumluapp.tutumlu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tutumluapp.tutumlu.databinding.ActivityMainBinding
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.PostgrestResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val supabaseUrl = "https://qbpruczdytiwqouzoztl.supabase.co"
        val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFicHJ1Y3pkeXRpd3FvdXpvenRsIiwicm9sZSI6ImFub24iLCJpYXQiOjE2OTcxMzE0ODUsImV4cCI6MjAxMjcwNzQ4NX0.aWqvr3-qOnJGQXZwk4GVNVXRnd2OKMOn3fkWF8Pm2UE"

        val supabase = createSupabaseClient(supabaseUrl, supabaseKey) {
            install(Postgrest)
        }
        @Serializable
        data class Product(
            @SerialName("price") val price: Double,
            @SerialName("product_id") val barcode: Long
        )

        val products = supabase.postgrest.from("prices")

        GlobalScope.launch {

            val result = async {
                //var i : PostgrestResult = products.insert(Product(17.5, 8690637939891))
                /*var i : PostgrestResult = products.select(columns = Columns.list("gtin, name")) {
                    filter(FilterOperation("name", FilterOperator.EQ,"%Algida%") )
                }
                Log.i("ErenTest",
                    i.body?.jsonArray?.map { it.jsonObject["gtin"]?.jsonPrimitive?.long }.toString()
                )*/

                delay(2000)
            }

            val asyncResult = result.await()
        }



            binding.btnSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
        }

        setContentView(binding.root)

    }
}


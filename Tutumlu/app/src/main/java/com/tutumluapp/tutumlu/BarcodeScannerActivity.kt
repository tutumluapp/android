package com.tutumluapp.tutumlu

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.journeyapps.barcodescanner.CaptureActivity


class BarcodeScannerActivity : CaptureActivity() {

    private var productName : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(intent.hasExtra("product")){
            productName = intent.getStringExtra("product")
        }

        // Start the barcode scanner
        val integrator = IntentIntegrator(this)
        integrator.setCaptureActivity(CaptureActivity::class.java) // Use the ZXing CaptureActivity
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            if (result.contents != null) {
                // Handle the scanned barcode value as needed
                showToast("Scanned Barcode: ${result.contents}")

                val data = Intent()
                data.putExtra("product", productName)
                data.putExtra("barcode", result.contents)
                setResult(RESULT_OK, data)
                finish()

            } else {
                val data = Intent()
                setResult(RESULT_CANCELED, data)
                finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}

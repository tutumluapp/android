package com.tutumluapp.tutumlu

import android.app.Activity
import androidx.core.app.ActivityCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.Manifest

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class BarcodeScannerTest {
    @Test
    fun barcodeScannerTest() {

        val scenario = ActivityScenario.launchActivityForResult(BarcodeScannerActivity::class.java)

        val result = scenario.result

        assertEquals(Activity.RESULT_OK, result.resultCode)

        val resultData = result.resultData
        val resultValue = resultData?.getStringExtra("barcode")

        assertEquals("ABC-abc-1234", resultValue)

        val activity = getActivityInstance(scenario)
        val isCameraPermissionGranted = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.CAMERA
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        assertEquals(true, isCameraPermissionGranted)

    }

    private fun getActivityInstance(scenario: ActivityScenario<BarcodeScannerActivity>): BarcodeScannerActivity {
        val activity = mutableListOf<BarcodeScannerActivity>()

        scenario.onActivity {
            activity.add(it)
        }

        return activity.first()
    }


}
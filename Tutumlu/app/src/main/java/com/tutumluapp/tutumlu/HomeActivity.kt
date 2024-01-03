package com.tutumluapp.tutumlu

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.tutumluapp.tutumlu.databinding.ActivityHomeBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class HomeActivity : AppCompatActivity() {

    private lateinit var binding:ActivityHomeBinding
    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)

        binding.hdrHome.ttHdr.text = "Home"
        binding.hdrHome.btnOptional.setImageResource(R.drawable.btn_profile)
        binding.hdrHome.btnOptional.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.btnSearch.btnHdr.text = "Search Item"
        binding.btnSearch.btnOptional.setImageResource(R.drawable.logo_search)
        binding.btnSearch.clickableLayout.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        binding.btnUpload.btnHdr.text = "Upload Slip"
        binding.btnUpload.btnOptional.setImageResource(R.drawable.logo_scan)
        binding.btnUpload.clickableLayout.setOnClickListener {
            //startActivity(Intent(this, UploadActivity::class.java))

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE)
            } else {
                dispatchTakePictureIntent()
            }
        }
        setContentView(binding.root)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
                //startActivity(Intent(this, BarcodeScannerActivity::class.java))
                // Permission granted, proceed with camera operations
                // Initialize and use the camera here
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
            }
        }
    }

    val REQUEST_IMAGE_CAPTURE = 1

    private var photoFile: File? = null
    private var currentPhotoPath: String = ""

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                photoFile = createImageFile()

                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "${BuildConfig.APPLICATION_ID}.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            val bitmap: Bitmap? = currentPhotoPath?.let { decodeSampledBitmapFromFile(it, 1080, 1920) }
            val options = TextRecognizerOptions.Builder()
                .build()

            val textRecognizer = TextRecognition.getClient(options)

            val image: InputImage? =
                bitmap?.let { InputImage.fromBitmap(it, 0) }

            if (image != null) {
                textRecognizer.process(image)
                    .addOnSuccessListener { text ->
                        val recognizedText = text.text
                        val intent = Intent(this, UploadActivity::class.java)

                        intent.putExtra("receipt", recognizedText)

                        startActivity(intent)

                        /*val textBlocks = text.textBlocks.toMutableList()
                        textBlocks.sortBy { it.boundingBox?.top }
                        val acceptanceRate = 0.02

                        val groupedTexts = mutableListOf<List<Text.TextBlock>>()
                        var currentLine = mutableListOf(textBlocks[0])

                        for (i in 1 until textBlocks.size) {
                            val currentY = textBlocks[i].boundingBox?.top
                            val previousY = textBlocks[i - 1].boundingBox?.bottom

                            if (currentY != null) {
                                if (currentY - previousY!! <= acceptanceRate) {
                                    currentLine.add(textBlocks[i])
                                } else {
                                    groupedTexts.add(currentLine.toList())
                                    currentLine = mutableListOf(textBlocks[i])
                                }
                            }
                        }

                        groupedTexts.add(currentLine.toMutableList())
                        for (line in groupedTexts) {
                            for (block in line) {
                                val lineText = block.text
                                Log.i("TEST", lineText)
                            }
                        }*/
                        /*for (block in text.textBlocks) {
                            for (line in block.lines) {
                                val lineText = line.text
                                Log.i("TEST", lineText)
                            }
                        }

                        Log.i("TEST", recognizedText)*/

                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
            }
        }
    }

    private fun decodeSampledBitmapFromFile(filePath: String, reqWidth: Int, reqHeight: Int): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(filePath, options)
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth

        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())

            inSampleSize = if (heightRatio > widthRatio) heightRatio else widthRatio
        }

        return inSampleSize
    }
}
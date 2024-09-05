package th.ac.rmutto.ralinthipandroid

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import th.ac.rmutto.ralinthipandroid.databinding.ActivityAddBinding
import java.io.File

class AddActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var textNameImage: TextView
    private var selectedImage : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val editBrand = findViewById<EditText>(R.id.editBrand)
        val editModel = findViewById<EditText>(R.id.editModel)
        val editYear = findViewById<EditText>(R.id.editYear)
        val editColor = findViewById<EditText>(R.id.editColor)
        val editPrice = findViewById<EditText>(R.id.editPrice)
        val editGearType = findViewById<EditText>(R.id.editGearType)
        val editFuelType = findViewById<EditText>(R.id.editFuelType)
        val editDoors = findViewById<EditText>(R.id.editDoors)
        val editSeats = findViewById<EditText>(R.id.editSeats)
        imageView = findViewById<ImageView>(R.id.imageView)
        textNameImage = findViewById<TextView>(R.id.text_name_img)
        val bt_upload_img = findViewById<Button>(R.id.bt_upload_img)

        bt_upload_img.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_IMAGE_PICKER)
        }

        val bt_add = findViewById<Button>(R.id.bt_add)
        bt_add.setOnClickListener {
            if (editBrand.text.toString() == "") {
                editBrand.error = "กรุณาระบุยี่ห้อรถยนต์"
                return@setOnClickListener
            }
            if (editModel.text.toString() == "") {
                editModel.error = "กรุณาระบุรุ่นรถยนต์"
                return@setOnClickListener
            }
            if (editYear.text.toString() == "") {
                editYear.error = "กรุณาระบุปีที่ผลิต"
                return@setOnClickListener
            }
            if (editColor.text.toString() == "") {
                editColor.error = "กรุณาระบุสีของรถ"
                return@setOnClickListener
            }
            if (editPrice.text.toString() == "") {
                editPrice.error = "กรุณาระบุราคาของรถ"
                return@setOnClickListener
            }
            if (editGearType.text.toString() == "") {
                editGearType.error = "กรุณาระบุชนิดของเกียร์"
                return@setOnClickListener
            }
            if (editFuelType.text.toString() == "") {
                editFuelType.error = "กรุณาระบุประเภทเชื้อเพลิง"
                return@setOnClickListener
            }
            if (editDoors.text.toString() == "") {
                editDoors.error = "กรุณาระบุจำนวนประตู"
                return@setOnClickListener
            }
            if (editSeats.text.toString() == "") {
                editSeats.error = "กรุณาระบุจำนวนที่นั่ง"
                return@setOnClickListener
            }

            // สร้าง Coroutine Scope
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val url = "http://10.13.1.206:3000/car"
                    val okHttpClient = OkHttpClient()

                    val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("brand", editBrand.text.toString())
                        .addFormDataPart("model", editModel.text.toString())
                        .addFormDataPart("year", editYear.text.toString())
                        .addFormDataPart("color", editColor.text.toString())
                        .addFormDataPart("price", editPrice.text.toString())
                        .addFormDataPart("gearType", editGearType.text.toString())
                        .addFormDataPart("fuelType", editFuelType.text.toString())
                        .addFormDataPart("doors", editDoors.text.toString())
                        .addFormDataPart("seats", editSeats.text.toString())

                    // Add image if present
                    selectedImage?.let {
                        // เพิ่มส่วนนี้เพื่อจัดการ URI และแปลงเป็นไฟล์อย่างถูกต้อง
                        val inputStream = contentResolver.openInputStream(it)
                        val file = File(cacheDir, "image.jpg")
                        inputStream?.use { input ->
                            file.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }

                        val requestFile = file.asRequestBody("image/jpeg".toMediaType())
                        builder.addFormDataPart("imageFile", file.name, requestFile)
                    }

                    val requestBody = builder.build()

                    val request: Request = Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build()

                    val response = okHttpClient.newCall(request).execute()

                    if (response.isSuccessful) {
                        val jsonResponse = JSONObject(response.body?.string() ?: "{}")
                        val status = jsonResponse.getString("status")
                        val message = jsonResponse.getString("message")

                        withContext(Dispatchers.Main) {
                            if (status == "true") {
                                Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()

                                // Redirect to main page
                                val intent = Intent(this@AddActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "ไม่สามารถเชื่อมต่อกับเซิร์ฟเวอร์ได้", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "เกิดข้อผิดพลาดในการเชื่อมต่อ", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        val bt_back = findViewById<Button>(R.id.bt_back)
        bt_back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_IMAGE_PICKER -> {
                    selectedImage = data?.data
                    if (selectedImage != null) {
                        val imageView = findViewById<ImageView>(R.id.imageView)
                        imageView.setImageURI(selectedImage)

                        val fileName = getFileNameFromUri(selectedImage!!)
                        textNameImage.text = fileName
                    }
                }
            }
        }
    }

    private fun getFileNameFromUri(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndexOrThrow("_display_name")
            if (it.moveToFirst()) {
                return it.getString(nameIndex)
            }
        }
        return uri.path?.substringAfterLast('/') ?: "Unknown file name"
    }

    companion object{
        private const val REQUEST_CODE_IMAGE_PICKER = 100
    }
}

package th.ac.rmutto.ralinthipandroid

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class ShowActivity : AppCompatActivity() {

    private lateinit var text_show_brand: TextView
    private lateinit var text_show_model: TextView
    private lateinit var text_show_year: TextView
    private lateinit var text_show_color: TextView
    private lateinit var text_show_price: TextView
    private lateinit var text_show_gearType: TextView
    private lateinit var text_show_fuelType: TextView
    private lateinit var text_show_doors: TextView
    private lateinit var text_show_seats: TextView
    private lateinit var imageView: ImageView
    private lateinit var image: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show)

        text_show_brand = findViewById<TextView>(R.id.text_show_brand)
        text_show_model = findViewById<TextView>(R.id.text_show_model)
        text_show_year = findViewById<TextView>(R.id.text_show_year)
        text_show_color = findViewById<TextView>(R.id.text_show_color)
        text_show_price = findViewById<TextView>(R.id.text_show_price)
        text_show_gearType = findViewById<TextView>(R.id.text_show_gearType)
        text_show_fuelType = findViewById<TextView>(R.id.text_show_fuelType)
        text_show_doors = findViewById<TextView>(R.id.text_show_doors)
        text_show_seats = findViewById<TextView>(R.id.text_show_seats)
        imageView = findViewById<ImageView>(R.id.imageShow)

        val carID = intent.getStringExtra("CAR_ID") ?: ""
        if (carID.isNotEmpty()) {
            searchCarDetails(carID)
        } else {
            Toast.makeText(this, "Car ID ไม่ถูกต้อง", Toast.LENGTH_SHORT).show()
        }

        val bt_back = findViewById<Button>(R.id.bt_back)
        bt_back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun searchCarDetails(carID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = "http://10.13.1.206:3000/car/$carID"// URL ของ API ที่ใช้ค้นหาข้อมูล
            val okHttpClient = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            try {
                val response = okHttpClient.newCall(request).execute()
                val responseBody = response.body?.string() ?: "{}"
                if (response.isSuccessful) {
                    val jsonResponse = JSONObject(responseBody)
                    val status = jsonResponse.getString("status")
                    if (status == "true") {
                        val data = jsonResponse.getJSONObject("data")

                        // ดึงข้อมูลจาก JSON
                        val brand = data.optString("brand", "-")
                        val model = data.optString("model", "-")
                        val year = data.optString("year", "-")
                        val color = data.optString("color", "-")
                        val price = data.optString("price", "-")
                        val gearType = data.optString("gearType", "-")
                        val fuelType = data.optString("fuelType", "-")
                        val doors = data.optString("doors", "-")
                        val seats = data.optString("seats", "-")
                        val imageShow = data.optString("imageFile","-")

                        // อัพเดท UI ใน thread หลัก
                        withContext(Dispatchers.Main) {
                            text_show_brand.text = brand
                            text_show_model.text = model
                            text_show_year.text = year
                            text_show_color.text = color
                            text_show_price.text = price
                            text_show_gearType.text = gearType
                            text_show_fuelType.text = fuelType
                            text_show_doors.text = doors
                            text_show_seats.text = seats
                            image = imageShow

                            if (image != null){
                                val image_url = getString(R.string.root_url) +
                                        getString(R.string.image_url) + image
                                Toast.makeText(this@ShowActivity, "ข้อมูลรูป", Toast.LENGTH_LONG)

                                Picasso.get()
                                    .load(image_url)
                                    .into(imageView)

                            }
                        }
                    } else {
                        val message = jsonResponse.optString("message", "เกิดข้อผิดพลาด")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@ShowActivity, message, Toast.LENGTH_LONG).show()
                        }
                    }

                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@ShowActivity,
                            "ไม่สามารถเชื่อมต่อกับเซิร์ฟเวอร์ได้",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ShowActivity, "เกิดข้อผิดพลาดในการค้นหา", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
}
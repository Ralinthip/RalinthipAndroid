package th.ac.rmutto.ralinthipandroid

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import th.ac.rmutto.ralinthipandroid.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var edit_search = findViewById<EditText>(R.id.edit_search)

        val bt_add_data = findViewById<Button>(R.id.bt_add_data)
        bt_add_data.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
            finish()
        }

        val bt_search = findViewById<Button>(R.id.bt_search)
        bt_search.setOnClickListener {
            val carID = edit_search.text.toString()

            if (carID.isNotEmpty()) {
                val intent = Intent(this, ShowActivity::class.java)
                intent.putExtra("CAR_ID", carID)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "กรุณาระบุ Car ID", Toast.LENGTH_SHORT).show()
            }

            val url = getString(R.string.root_url) + getString(R.string.show_car_url) + carID
//
//            val okHttpClient = OkHttpClient()
//            val formBody: RequestBody = FormBody.Builder()
//                .add("carID",editTextUsername.text.toString())
//                .add("password",editTextPassword.text.toString())
//                .build()
//            val request: Request = Request.Builder()
//                .url(url)
//                .post(formBody)
//                .build()





        }
    }
}
package com.example.anabada

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.anabada.databinding.ActivityMainBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    var login:LoginRes? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://anabada.du.r.appspot.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: ApiService = retrofit.create(ApiService::class.java)

        binding.loginBtn.setOnClickListener {
            val uid = binding.id.text.toString()
            val upw = binding.pw.text.toString()

            service.reqLogin(uid, upw).enqueue(object : Callback<LoginRes>{
                override fun onFailure(call: Call<LoginRes>, t: Throwable) {
                    val dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("Failed connection")
                    dialog.show()
                }

                override fun onResponse(call: Call<LoginRes>, response: Response<LoginRes>) {
                    login = response.body()
                    val dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("success: " + login?.success.toString())
                    dialog.setMessage("result code: " + login?.resultCode + "\nid: " + login?.id.toString() + "\nnickname: " + login?.nickname)
                    dialog.show()
                }
            })
        }

        binding.signupBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

}

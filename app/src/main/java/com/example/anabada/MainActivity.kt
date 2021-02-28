package com.example.anabada

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.anabada.databinding.ActivityMainBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    var login:Res? = null
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

            service.reqLogin(uid, upw).enqueue(object : Callback<Res>{
                override fun onFailure(call: Call<Res>, t: Throwable) {
                    val dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("Failed connection")
                    dialog.show()
                }

                override fun onResponse(call: Call<Res>, response: Response<Res>) {
                    login = response.body()
                    val dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle(login?.success.toString())
                    dialog.setMessage(login?.resultCode + login?.id.toString() + login?.nickname)
                    dialog.show()
                }
            })
        }

    }

    /*
    private fun createOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(interceptor)
        return builder.build()
    }
    */

}

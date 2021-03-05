package com.example.anabada

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.anabada.databinding.ActivitySignupBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignUpActivity: AppCompatActivity() {

    var signup:SignUpRes? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit = Retrofit.Builder()
                .baseUrl("https://anabada.du.r.appspot.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val service: ApiService = retrofit.create(ApiService::class.java)

        binding.signupBtn.setOnClickListener {
            val uid = binding.id.text.toString()
            val upw = binding.pw.text.toString()
            val nickname = binding.nickname.text.toString()

            service.reqSignUp(uid, upw, nickname).enqueue(object : Callback<SignUpRes> {
                override fun onFailure(call: Call<SignUpRes>, t: Throwable) {
                    val dialog = AlertDialog.Builder(this@SignUpActivity)
                    dialog.setTitle("Failed connection")
                    dialog.show()
                }

                override fun onResponse(call: Call<SignUpRes>, response: Response<SignUpRes>) {
                    signup = response.body()
                    val dialog = AlertDialog.Builder(this@SignUpActivity)
                    dialog.setTitle("success: " + signup?.success.toString())
                    dialog.setMessage("result code: " + signup?.resultCode)
                    dialog.show()
                }
            })
        }

    }
}
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

        // SharedPreferences 안에 값이 저장되어 있지 않을 때 -> Login
        if(MySharedPreferences.getUserId(this).isNullOrBlank()
                || MySharedPreferences.getUserPass(this).isNullOrBlank()) {
            Login(binding, service)
        }
        else { // SharedPreferences 안에 값이 저장되어 있을 때 -> 게시판로 이동
            Toast.makeText(this, "${MySharedPreferences.getUserId(this)}님 자동 로그인 되었습니다.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, BoardActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.signupBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.skipBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, BoardActivity::class.java)
            startActivity(intent)
        }
    }

    fun Login(binding: ActivityMainBinding, service: ApiService) {
        binding.loginBtn.setOnClickListener {
            val uid = binding.id.text.toString()
            val upw = binding.pw.text.toString()

            service.reqLogin(uid, upw).enqueue(object : Callback<LoginRes>{
                override fun onFailure(call: Call<LoginRes>, t: Throwable) {
                    val dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("login api\nFailed connection")
                    dialog.show()
                }

                override fun onResponse(call: Call<LoginRes>, response: Response<LoginRes>) {
                    login = response.body()
                    Toast.makeText(this@MainActivity, "login api\nsuccess: " + login?.success.toString() +
                            "\nresult code: " + login?.resultCode + "\nid: " + login?.id.toString() +
                            "\nnickname: " + login?.nickname, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, BoardActivity::class.java)
                    startActivity(intent)
                }
            })
        }
    }
}

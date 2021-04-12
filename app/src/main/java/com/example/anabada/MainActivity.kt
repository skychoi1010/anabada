package com.example.anabada

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.anabada.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    
    var loginRes: LoginRes? = null
    private val api = ApiService.create(this)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Anabada)
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences 안에 값이 저장되어 있지 않을 때 -> Login
        if(MySharedPreferences.getUserNick(this).isBlank()) {
            initView(binding)
        } else { // SharedPreferences 안에 값이 저장되어 있을 때 -> 게시판으로 이동s
            val uid = MySharedPreferences.getUserId(this)
            val upw = MySharedPreferences.getUserPass(this)

            api.reqLogin(uid, upw).enqueue(object : Callback<LoginRes>{
                override fun onFailure(call: Call<LoginRes>, t: Throwable) {
                    val dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("login api\nFailed connection")
                    dialog.show()
                }

                override fun onResponse(call: Call<LoginRes>, response: Response<LoginRes>) {
                    loginRes = response.body()
                    if (loginRes?.success == null) {
                        Toast.makeText(this@MainActivity, "아이디와 비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "${MySharedPreferences.getUserNick(this@MainActivity)}님 자동 로그인 되었습니다.", Toast.LENGTH_SHORT).show()
                        Intent(this@MainActivity, BoardActivity::class.java).apply {
                            startActivity(this)
                        }
                        finish()
                    }
                }
            })
        }


    }

    private fun initView(binding: ActivityMainBinding) {
        binding.loginBtn.setOnClickListener {
            val uid = binding.id.text.toString()
            val upw = binding.pw.text.toString()
            login(uid, upw)
        }

        binding.signupBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.skipBtn.setOnClickListener {
            MySharedPreferences.setUserId(this, "no")
            val intent = Intent(this@MainActivity, BoardActivity::class.java)
            startActivity(intent)
        }

    }

    private fun login(uid: String, upw: String) {
        api.reqLogin(uid, upw).enqueue(object : Callback<LoginRes> {
            override fun onFailure(call: Call<LoginRes>, t: Throwable) {
                val dialog = AlertDialog.Builder(this@MainActivity)
                dialog.setTitle("login api\nFailed connection")
                dialog.show()
            }

            override fun onResponse(call: Call<LoginRes>, response: Response<LoginRes>) {
                loginRes = response.body()
                if (loginRes?.success == null) {
                    Toast.makeText(this@MainActivity, "아이디와 비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        this@MainActivity, "login api\nsuccess: " + loginRes?.success.toString() +
                                "\nresult code: " + loginRes?.resultCode + "\nid: " + loginRes?.id.toString() +
                                "\nnickname: " + loginRes?.nickname, Toast.LENGTH_SHORT
                    ).show()
                    MySharedPreferences.setUserId(this@MainActivity, uid)
                    MySharedPreferences.setUserPass(this@MainActivity, upw)
                    MySharedPreferences.setUserNick(
                        this@MainActivity,
                        loginRes?.nickname.toString()
                    )
                    Intent(this@MainActivity, BoardActivity::class.java).apply {
                        startActivity(this)
                    }
                }
            }
        })
    }
}

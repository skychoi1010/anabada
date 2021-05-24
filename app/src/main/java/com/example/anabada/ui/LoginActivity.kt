package com.example.anabada.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.anabada.*
import com.example.anabada.databinding.ActivityLoginBinding
import com.example.anabada.network.ApiService
import com.example.anabada.network.LoginRes
import com.example.anabada.repository.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    var loginRes: LoginRes? = null
    private val api = ApiService.create(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Anabada)
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView(binding)
    }

    private fun initView(binding: ActivityLoginBinding) {
        binding.loginBtn.setOnClickListener {
            val uid = binding.id.text.toString()
            val upw = binding.pw.text.toString()
            login(uid, upw)
        }

        binding.signupBtn.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.skipBtn.setOnClickListener {
            MySharedPreferences.setUserId(this, "no")
            val intent = Intent(this@LoginActivity, HomeFragment::class.java)
            startActivity(intent)
        }

    }

    private fun login(uid: String, upw: String) {
        api.reqLogin(uid, upw).enqueue(object : Callback<LoginRes> {
            override fun onFailure(call: Call<LoginRes>, t: Throwable) {
                val dialog = AlertDialog.Builder(this@LoginActivity)
                dialog.setTitle("login api\nFailed connection")
                dialog.show()
            }

            override fun onResponse(call: Call<LoginRes>, response: Response<LoginRes>) {
                loginRes = response.body()
                if (loginRes?.success == null) {
                    Toast.makeText(this@LoginActivity, "아이디와 비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT)
                            .show()
                } else {
                    Toast.makeText(
                            this@LoginActivity, "login api\nsuccess: " + loginRes?.success.toString() +
                            "\nresult code: " + loginRes?.resultCode + "\nid: " + loginRes?.id.toString() +
                            "\nnickname: " + loginRes?.nickname, Toast.LENGTH_SHORT
                    ).show()
                    MySharedPreferences.setUserId(this@LoginActivity, uid)
                    MySharedPreferences.setUserPass(this@LoginActivity, upw)
                    MySharedPreferences.setUserNick(
                            this@LoginActivity,
                            loginRes?.nickname.toString()
                    )
                    Intent(this@LoginActivity, HomeFragment::class.java).apply {
                        startActivity(this)
                    }
                }
            }
        })
    }
}

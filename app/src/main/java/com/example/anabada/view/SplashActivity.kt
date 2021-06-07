package com.example.anabada.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.anabada.network.ApiService
import com.example.anabada.network.LoginRes
import com.example.anabada.repository.SharedPreferencesManager

class SplashActivity : AppCompatActivity() {
    var loginRes: LoginRes? = null
    private val api = ApiService.create(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SharedPreferences 안에 값이 저장되어 있지 않을 때 -> Login
        if (SharedPreferencesManager.getUserNick(this).isBlank()) {
            Intent(this@SplashActivity, LoginActivity::class.java).apply {
                startActivity(this)
            }
            finish()
        } else { // SharedPreferences 안에 값이 저장되어 있을 때 -> 게시판으로 이동
            val uid = SharedPreferencesManager.getUserId(this)
            val upw = SharedPreferencesManager.getUserPass(this)

            //TODO temporary fix - must delete afterwards
            Intent(this@SplashActivity, LoginActivity::class.java).apply {
                startActivity(this)
            }
            finish()
//            api.reqLogin(uid, upw).enqueue(object : Callback<LoginRes> {
//                override fun onFailure(call: Call<LoginRes>, t: Throwable) {
//                    Toast.makeText(this@SplashActivity, "login api\nFailed connection", Toast.LENGTH_SHORT).show()
//                }
//
//                override fun onResponse(call: Call<LoginRes>, response: Response<LoginRes>) {
//                    loginRes = response.body()
//                    if (loginRes?.success == null) {
//                        Intent(this@SplashActivity, LoginActivity::class.java).apply {
//                            startActivity(this)
//                        }
//                        finish()
//                    } else {
//                        Toast.makeText(this@SplashActivity, "${SharedPreferencesManager.getUserNick(this@SplashActivity)}님 자동 로그인 되었습니다.", Toast.LENGTH_SHORT).show()
//                        Intent(this@SplashActivity, MainActivity::class.java).apply {
//                            startActivity(this)
//                        }
//                        finish()
//                    }
//                }
//            })
        }

    }
}
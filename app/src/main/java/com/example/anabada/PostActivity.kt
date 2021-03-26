package com.example.anabada

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.anabada.databinding.ActivityPostBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PostActivity: AppCompatActivity() {

    var loginRes: LoginRes? = null
    var postContentRes: PostContentRes? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val api = ApiService.create(this)

        binding.btnPostContent.setOnClickListener {
            val title = binding.tvPostContentTitle.text.toString()
            val price = binding.tvPostContentPrice.text.toString()
            val contents = binding.tvPostContentContents.text.toString()
            if (MySharedPreferences.getUserId(this) == "no") { // need to login
                Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                Intent(this@PostActivity, MainActivity::class.java).apply {
                    startActivity(this)
                }
            } else { //TODO 로그인 세션 만료 시 예외 처리 (아직 api 없음)
                if (title.isNotEmpty() && price.isNotEmpty() && contents.isNotEmpty()){ // all contents not null
                    api.reqPostContent(title, price.toInt(), contents, 1).enqueue(object : Callback<PostContentRes> {
                        override fun onFailure(call: Call<PostContentRes>, t: Throwable) {
                            Toast.makeText(this@PostActivity, "post content api\nFailed connection", Toast.LENGTH_SHORT).show()
                        }

                        override fun onResponse(call: Call<PostContentRes>, response: Response<PostContentRes>) {
                            postContentRes = response.body()
                            Toast.makeText(this@PostActivity, "post content api\nresult: " + postContentRes?.resultCode.toString() +
                                    "\nid: " + postContentRes?.id.toString(), Toast.LENGTH_SHORT).show()
                            Intent(this@PostActivity, BoardActivity::class.java).apply {
                                startActivity(this)
                            }
                        }
                    })
                    /*
                    if (loginRes?.success == true) {
                    } else {
                        Toast.makeText(this@PostActivity, "not logged in\nnickname: " + loginRes?.nickname.toString() + "\nsuccess: " + loginRes?.resultCode.toString(), Toast.LENGTH_SHORT).show()
                    }
                    api.reqLogin(MySharedPreferences.getUserId(this), MySharedPreferences.getUserPass(this)).enqueue(object : Callback<LoginRes> {
                        override fun onFailure(call: Call<LoginRes>, t: Throwable) {
                            Toast.makeText(this@PostActivity, "post content api\nFailed connection", Toast.LENGTH_SHORT).show()
                        }

                        override fun onResponse(call: Call<LoginRes>, response: Response<LoginRes>) {
                            loginRes = response.body()

                        }
                    })

                     */
                } else { // missing contents
                    Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
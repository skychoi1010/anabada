package com.example.anabada

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.anabada.databinding.ActivityPostBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PostActivity: AppCompatActivity() {

    var postContentRes: PostContentRes? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://anabada.du.r.appspot.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: ApiService = retrofit.create(ApiService::class.java)

        binding.btnPostContent.setOnClickListener {
            val title = binding.tvPostContentTitle.text.toString()
            val price = binding.tvPostContentPrice.text.toString().toInt()
            val contents = binding.tvPostContentContents.text.toString()

            service.reqPostContent(title, price, contents, 0).enqueue(object : Callback<PostContentRes> {
                override fun onFailure(call: Call<PostContentRes>, t: Throwable) {
                    Toast.makeText(this@PostActivity, "post content api\nFailed connection", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<PostContentRes>, response: Response<PostContentRes>) {
                    postContentRes = response.body()
                    Toast.makeText(this@PostActivity, "post content api\nresult: " + postContentRes?.result.toString() +
                            "\nid: " + postContentRes?.id.toString(), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@PostActivity, BoardActivity::class.java)
                    startActivity(intent)
                }
            })
        }
    }

}
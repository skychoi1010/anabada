package com.example.anabada

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.viewbinding.BuildConfig
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestOptions
import com.example.anabada.databinding.ActivityPostBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.jar.Manifest

class PostActivity : AppCompatActivity() {

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
                if (title.isNotEmpty() && price.isNotEmpty() && contents.isNotEmpty()) { // all contents not null
                    api.reqPostContent(title, price.toInt(), contents, 1)
                        .enqueue(object : Callback<PostContentRes> {
                            override fun onFailure(call: Call<PostContentRes>, t: Throwable) {
                                Toast.makeText(
                                    this@PostActivity,
                                    "post content api\nFailed connection",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            override fun onResponse(
                                call: Call<PostContentRes>,
                                response: Response<PostContentRes>
                            ) {
                                postContentRes = response.body()
                                Toast.makeText(
                                    this@PostActivity,
                                    "post content api\nresult: " + postContentRes?.resultCode.toString() +
                                            "\nid: " + postContentRes?.id.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
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
/*
    private fun selectCamera() {
        var permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (permission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQ_CAMERA_PERMISSION
            )
        } else {
            var state = Environment.getExternalStorageState()
            if (TextUtils.equals(
                    state,
                    Environment.MEDIA_MOUNTED
                )
            ) {
                var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.resolveActivity(packageManager)?.let {
                    var photoFile: File? = createImageFile()
                    photoFile?.let {
                        var photoUri = FileProvider.getUriForFile(
                            this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            it
                        )
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                        startActivityForResult(intent, REQ_IMAGE_CAPTURE)
                    }
                }
            }
        }
    }

    private fun createImageFile(): File { // 사진이 저장될 폴더 있는지 체크
        var file = File(Environment.getExternalStorageDirectory(), "/path/")
        if (!file.exists()) file.mkdir()
        var imageName = "fileName.jpeg"
        var imageFile =
            File("${Environment.getExternalStorageDirectory().absoluteFile}/path/", "$imageName")
        imagePath = imageFile.absolutePath return imageFile
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQ_IMAGE_CAPTURE -> {
                    imagePath?.apply {
                        ctSelectImage.visibility = View.VISIBLE
                        GlideUtil.loadImage(this,
                            RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true),
                            imagePath,
                            imageView = ivSelectImage,
                            requestListener = object : RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    hideLoading()
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    hideLoading() return false
                                }
                            }) checkInput ()
                    }
                }
            }
        }
    }


 */

}
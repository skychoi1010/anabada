package com.example.anabada

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.anabada.databinding.ActivityPostBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class PostActivity : AppCompatActivity() {

    var healthCheckRes: HealthCheckRes? = null
    var postContentRes: PostContentRes? = null
    var postImageRes: PostImageRes? = null
    private val api = ApiService.create(this)
    private val apiImg = ApiService.createImg(this)
    private val pickImage = 100
    private var imageUri: Uri? = null
    private var binding: ActivityPostBinding? = null
    private var body : MultipartBody.Part? = null

    val CROP_FROM_IMAGE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initView(binding!!)

        binding!!.btnPickImage.setOnClickListener {
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI).apply {
                startActivityForResult(this, pickImage)
            }
        }
    }

    private fun initView(binding: ActivityPostBinding) {

        binding.appbar.toolbarFin.setOnClickListener {
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
                                Toast.makeText(this@PostActivity, "post content api\nFailed connection", Toast.LENGTH_SHORT).show()
                            }

                            override fun onResponse(call: Call<PostContentRes>, response: Response<PostContentRes>) {
                                postContentRes = response.body()
                                Toast.makeText(
                                        this@PostActivity,
                                        "post content api\nresult: " + postContentRes?.resultCode.toString() +
                                                "\nid: " + postContentRes?.id.toString(),
                                        Toast.LENGTH_SHORT
                                ).show()
                                /*Intent(this@PostActivity, BoardActivity::class.java).apply {
                                    startActivity(this)
                                }
                                */
                            }
                        })
                    apiImg.reqPostImage(body!!)
                        .enqueue(object : Callback<PostImageRes> {
                            override fun onFailure(call: Call<PostImageRes>, t: Throwable) {
                                Toast.makeText(this@PostActivity, "post content api\nFailed connection", Toast.LENGTH_SHORT).show()
                            }

                            override fun onResponse(call: Call<PostImageRes>, response: Response<PostImageRes>) {
                                postImageRes = response.body()
                                Toast.makeText(this@PostActivity, "post image api\nresult: " + postContentRes?.resultCode.toString() +
                                        "\nid: " + postContentRes?.id.toString(), Toast.LENGTH_SHORT).show()
                                Intent(this@PostActivity, BoardActivity::class.java).apply {
                                    startActivity(this)
                                }
                            }
                        })
                } else { // missing contents
                    Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == CROP_FROM_IMAGE) {
            val bundle = data?.extras!!
            val test = bundle.getParcelable<Bitmap>("data")
        }
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            imageUri?.let { resize(this, it, 20) }
            binding!!.sivPostImg.setImageURI(imageUri)
            val photoUri = data?.data
            cropImage(photoUri)
            binding!!.btnPickImage.text = "1/5"
            //creating a file

            val file = File(saveBitmapToJpeg(resize(this, imageUri!!, 20)!!, "image").absolutePath)
            val requestBody : RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            body = MultipartBody.Part.createFormData("image", file.name, requestBody)
            getPath(imageUri!!)?.let { Log.d("imgid", it) }
            /*
            apiImg.reqPostImage(body!!)
                    .enqueue(object : Callback<PostImageRes> {
                        override fun onFailure(call: Call<PostImageRes>, t: Throwable) {
                            Toast.makeText(this@PostActivity, "post content api\nFailed connection", Toast.LENGTH_SHORT).show()
                        }

                        override fun onResponse(call: Call<PostImageRes>, response: Response<PostImageRes>) {
                            postImageRes = response.body()
                            Toast.makeText(this@PostActivity, "post image api\nresult: " + postContentRes?.resultCode.toString() +
                                    "\nid: " + postContentRes?.id.toString(), Toast.LENGTH_SHORT).show()
                            Intent(this@PostActivity, BoardActivity::class.java).apply {
                                startActivity(this)
                            }
                        }
                    })

            apiImg.reqHealthCheck(body!!)
                .enqueue(object : Callback<HealthCheckRes> {
                    override fun onFailure(call: Call<HealthCheckRes>, t: Throwable) {
                        Toast.makeText(this@PostActivity, "healthCheck api\nFailed connection", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<HealthCheckRes>, response: Response<HealthCheckRes>) {
                        healthCheckRes = response.body()
                        Toast.makeText(this@PostActivity, "healthcheck\nresult: " + healthCheckRes?.resultCode.toString() +
                                "\nsuccess: " + healthCheckRes?.success.toString(), Toast.LENGTH_SHORT).show()
                    }
                })

             */

        }
    }
    private fun saveBitmapToJpeg(bitmap: Bitmap, name: String): File {

        //내부저장소 캐시 경로를 받아옵니다.
        val storage = cacheDir

        //저장할 파일 이름
        val fileName = "$name.jpg"

        //storage 에 파일 인스턴스를 생성합니다.
        val tempFile = File(storage, fileName)
        try {

            // 자동으로 빈 파일을 생성합니다.
            tempFile.createNewFile()

            // 파일을 쓸 수 있는 스트림을 준비합니다.
            val out = FileOutputStream(tempFile)

            // compress 함수를 사용해 스트림에 비트맵을 저장합니다.
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)

            // 스트림 사용후 닫아줍니다.
            out.close()
        } catch (e: FileNotFoundException) {
            Log.e("MyTag", "FileNotFoundException : " + e.message)
        } catch (e: IOException) {
            Log.e("MyTag", "IOException : " + e.message)
        }
        return tempFile
    }

    fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(uri, projection, null, null, null)
        val column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }
    private fun getRealPathFromURI(contentURI: Uri): String? {
        val filePath: String?
        val cursor: Cursor? = contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) {
            filePath = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            filePath = cursor.getString(idx)
            cursor.close()
        }
        return filePath
    }

    private fun cropImage(imageUri: Uri?) {
        val intent = getCropIntent(imageUri)
        startActivityForResult(intent, CROP_FROM_IMAGE)
    }

    private fun getCropIntent(inputUri: Uri?): Intent {
        val intent = Intent("com.android.camera.action.CROP")
        intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.setDataAndType(inputUri, "image/*")
        intent.putExtra("aspectX", 4)
        intent.putExtra("aspectY", 3)
        intent.putExtra("outputX", 400)
        intent.putExtra("outputY", 300)
        intent.putExtra("scale", true)

        //intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("return-data", true)
        return intent
    }

    private fun resize(context: Context, uri: Uri, resize: Int): Bitmap? {
        var resizeBitmap: Bitmap? = null
        val options = BitmapFactory.Options()
        try {
            BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri), null, options) // 이미지의 크기를 options 에 담아줌
            var width = options.outWidth
            var height = options.outHeight
            var samplesize = 4 // 숫자가 클수록 용량이 작아짐, 4 라고 하면 4픽셀을 1픽셀로 만들어줌
            while (true) { //가로세로 크기를 리사이즈크기에 최대한 맞춰서 작을때까지 반복함.
                if (width / 2 < resize || height / 2 < resize) break
                width /= 2
                height /= 2
                samplesize *= 2
            }
            options.inSampleSize = samplesize
            val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri), null, options) //정해진 샘플사이즈로 비트맵을 다시만든다
            resizeBitmap = bitmap
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return resizeBitmap
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
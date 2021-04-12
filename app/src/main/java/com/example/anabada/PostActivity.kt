package com.example.anabada

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
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
import kotlin.system.exitProcess


class PostActivity : AppCompatActivity() {

    var healthCheckRes: HealthCheckRes? = null
    var postContentRes: PostContentRes? = null
    var postImageRes: PostImageRes? = null
    private val api = ApiService.create(this)
    private val apiImg = ApiService.createImg(this)
    private val pickImage = 100
    private var imagePath: String? = null
    private var imageUri: Uri? = null
    private var binding: ActivityPostBinding? = null
    private var body : MultipartBody.Part? = null
    var imageId: Int = 0

    private val CROP_FROM_IMAGE = 1000

    private val REQ_CAMERA_PERMISSION = 111
    private val REQ_GALLERY = 112
    private val REQ_IMAGE_CAPTURE = 113
    private val REQ_STORAGE_PERMISSION = 114

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initView(binding!!)
    }


    private fun initView(binding: ActivityPostBinding) {

        binding.btnCamera.setOnClickListener {
            selectCamera()
        }

        binding.btnPickImage.setOnClickListener {
//            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI).apply {
//                startActivityForResult(this, pickImage)
//            }
            selectGallery()
        }

        var time: Long = 0
        binding.appbar.toolbarFin.setOnClickListener {
            if (System.currentTimeMillis() - time >= 10000) {
                time = System.currentTimeMillis()
                Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
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
                        api.reqPostContent(title, price.toInt(), contents, this@PostActivity.imageId)
                                .enqueue(object : Callback<PostContentRes> {
                                    override fun onFailure(call: Call<PostContentRes>, t: Throwable) {
                                        Toast.makeText(this@PostActivity, "post content api\nFailed connection", Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onResponse(call: Call<PostContentRes>, response: Response<PostContentRes>) {
                                        postContentRes = response.body()
                                        Toast.makeText(this@PostActivity, "post content api\nresult: " + postContentRes?.resultCode.toString() + "\nid: " + postContentRes?.id.toString(), Toast.LENGTH_SHORT).show()
                                        Intent(this@PostActivity, BoardActivity::class.java).apply {
                                            startActivity(this)
                                        }

                                    }
                                })

                    } else { // missing contents
                        Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
                    }

                }

            } else {
                //
            }

        }

    }


    /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == CROP_FROM_IMAGE) {
            val bundle = data?.extras!!
            val test = bundle.getParcelable<Bitmap>("data")

        }
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            imageUri?.let { resize(this, it, 300) }
            cropImage(imageUri)

            ///
            binding!!.sivPostImg.setImageURI(imageUri)
            binding!!.btnPickImage.text = "1/5"
            //creating a file

            val file = File(saveBitmapToJpeg(resize(this, imageUri!!, 300)!!, "image").absolutePath)
            val requestBody : RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            body = MultipartBody.Part.createFormData("image", file.name, requestBody)

            apiImg.reqPostImage(body!!)
                .enqueue(object : Callback<PostImageRes> {
                    override fun onFailure(call: Call<PostImageRes>, t: Throwable) {
                        Toast.makeText(this@PostActivity, "post content api\nFailed connection", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<PostImageRes>, response: Response<PostImageRes>) {
                        postImageRes = response.body()
                        this@PostActivity.imageId = postImageRes?.id!!
                        Toast.makeText(this@PostActivity, "post image api\nresult: " + postImageRes?.resultCode.toString() +
                                "\nid: " + postImageRes?.id.toString(), Toast.LENGTH_SHORT).show()
                    }
                })


            ///
            /*
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

 */

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

//    fun getPath(uri: Uri?): String? {
//        val projection = arrayOf(MediaStore.Images.Media.DATA)
//        val cursor = managedQuery(uri, projection, null, null, null)
//        val column_index = cursor
//                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//        cursor.moveToFirst()
//        return cursor.getString(column_index)
//    }

//    private fun getRealPathFromURI(contentURI: Uri): String? {
//        val filePath: String?
//        val cursor: Cursor? = contentResolver.query(contentURI, null, null, null, null)
//        if (cursor == null) {
//            filePath = contentURI.path
//        } else {
//            cursor.moveToFirst()
//            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
//            filePath = cursor.getString(idx)
//            cursor.close()
//        }
//        return filePath
//    }

//    private fun cropImage(imageUri: Uri?) {
//        val intent = getCropIntent(imageUri)
//        startActivityForResult(intent, CROP_FROM_IMAGE)
//    }
//
//    private fun getCropIntent(inputUri: Uri?): Intent {
//        val intent = Intent("com.android.camera.action.CROP")
//        intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//        intent.setDataAndType(inputUri, "image/*")
//        intent.putExtra("aspectX", 5)
//        intent.putExtra("aspectY", 5)
//        intent.putExtra("outputX", 300)
//        intent.putExtra("outputY", 300)
//        intent.putExtra("scale", true)
//
//        //intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
//        intent.putExtra("return-data", true)
//        return intent
//    }

    private fun resize(context: Context, uri: Uri, resize: Int): Bitmap? {
        var resizeBitmap: Bitmap? = null
        val options = BitmapFactory.Options()
        try {
            BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri), null, options) // 이미지의 크기를 options 에 담아줌
            var width = options.outWidth
            var height = options.outHeight
            var samplesize = 1 // 숫자가 클수록 용량이 작아짐, 4 라고 하면 4픽셀을 1픽셀로 만들어줌
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

    private fun selectCamera() {

        var permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (permission == PackageManager.PERMISSION_DENIED) {
            // 권한 없어서 요청
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQ_CAMERA_PERMISSION)
        } else {
            // 권한 있음
            var state = Environment.getExternalStorageState()
            if (TextUtils.equals(state, Environment.MEDIA_MOUNTED)) {
//                var intent1 = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                intent1.resolveActivity(packageManager)?.let {
//                    val photoFile: File? = createImageFile()
//                    photoFile?.let {
//                        val photoUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", it)
//                        Log.d("camera intent2", photoUri.toString())
//                        intent1.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
//                        startActivityForResult(intent1, REQ_IMAGE_CAPTURE)
//                    }
//                }
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(packageManager) != null) {
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                    }
                    if (photoFile != null) {
                        imageUri = FileProvider.getUriForFile(this, packageName, photoFile)
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                        startActivityForResult(takePictureIntent, REQ_IMAGE_CAPTURE)
                    }
                }
            }
        }
    }

    private fun createImageFile(): File {
        // 사진이 저장될 폴더 있는지 체크
        var file = File(Environment.getExternalStorageDirectory(), "/path/")
        if (!file.exists()) file.mkdir()
        var imageName = "fileName.jpeg"
        var imageFile = File("${Environment.getExternalStorageDirectory().absolutePath}/path/", "$imageName")
        this.imagePath = imageFile.absolutePath
        this.imageUri = Uri.fromFile(imageFile)
        Log.d("camera intent1", imageUri.toString())
        Log.d("camera intent1", imagePath.toString())
        return imageFile
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQ_IMAGE_CAPTURE -> {
                    Log.d("camera intent", "passed")
                    Log.d("camera intent", imagePath.toString())
                    data?.data?.let {
                        Glide.with(this@PostActivity)
                                .load(it)
                                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                                .into(binding?.sivPostImg!!)
                    }
                    val file = File(saveBitmap(getResizePicture(imagePath!!)!!))
                    val requestBody: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                    body = MultipartBody.Part.createFormData("image", file.name, requestBody)

                    apiImg.reqPostImage(body!!)
                            .enqueue(object : Callback<PostImageRes> {
                                override fun onFailure(call: Call<PostImageRes>, t: Throwable) {
                                    Toast.makeText(this@PostActivity, "post content api\nFailed connection", Toast.LENGTH_SHORT).show()
                                }

                                override fun onResponse(call: Call<PostImageRes>, response: Response<PostImageRes>) {
                                    postImageRes = response.body()
                                    this@PostActivity.imageId = postImageRes?.id!!
                                    Toast.makeText(this@PostActivity, "post image api\nresult: " + postImageRes?.resultCode.toString() +
                                            "\nid: " + postImageRes?.id.toString(), Toast.LENGTH_SHORT).show()
                                }
                            })
                }

                REQ_GALLERY -> {
                    data?.data?.let { it ->
                        //showLoading()
                        imagePath = getRealPathFromURI(it)
                        Glide.with(this)
                                .load(it)
                                .apply(RequestOptions())
                                .into(binding?.sivPostImg!!)

                        val file = File(saveBitmapToJpeg(resize(this, it, 300)!!, "image").absolutePath)
                        val requestBody: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                        body = MultipartBody.Part.createFormData("image", file.name, requestBody)

                        apiImg.reqPostImage(body!!)
                                .enqueue(object : Callback<PostImageRes> {
                                    override fun onFailure(call: Call<PostImageRes>, t: Throwable) {
                                        Toast.makeText(this@PostActivity, "post content api\nFailed connection", Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onResponse(call: Call<PostImageRes>, response: Response<PostImageRes>) {
                                        postImageRes = response.body()
                                        this@PostActivity.imageId = postImageRes?.id!!
                                        Toast.makeText(this@PostActivity, "post image api\nresult: " + postImageRes?.resultCode.toString() +
                                                "\nid: " + postImageRes?.id.toString(), Toast.LENGTH_SHORT).show()
                                    }
                                })

                    }

                }
            }

        }
    }


    private fun selectGallery() {

        var writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        var readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (writePermission == PackageManager.PERMISSION_DENIED || readPermission == PackageManager.PERMISSION_DENIED) {
            // 권한 없어서 요청
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), REQ_STORAGE_PERMISSION)
        } else {
            // 권한 있음
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI).apply {
                data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                type = "image/*"
                startActivityForResult(this, REQ_GALLERY)
            }
        }

    }

    private fun getRealPathFromURI(uri: Uri): String? {

        var buildName = Build.MANUFACTURER
        if (buildName.equals("Xiaomi")) {
            return uri.path
        }

        var columnIndex = 0
        var proj = arrayOf(MediaStore.Images.Media.DATA)
        var cursor = contentResolver.query(uri, proj, null, null, null)
        if (cursor!!.moveToFirst()) {
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }
        return cursor.getString(columnIndex)
    }

    private fun getResizePicture(imagePath: String): Bitmap? {
        var options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imagePath, options)

        var resize = 1000
        var width = options.outWidth
        var height = options.outHeight
        var sampleSize = 1
        while (true) {
            if (width / 2 < resize || height / 2 < resize)
                break
            width /= 2
            height /= 2
            sampleSize *= 2

        }
        options.inSampleSize = sampleSize
        options.inJustDecodeBounds = false

        var resizeBitmap = BitmapFactory.decodeFile(imagePath, options)

//        // 회전값 조정
//        var exit = ExifInterface(imagePath)
//        var exifDegree = 0
//        exit?.let {
//            var exifOrientation = it.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
//            exifDegree = exifOrientationToDegrees(exifOrientation)
//        }
//
//        return rotateBitmap(resizeBitmap, exifDegree.toFloat())

        return resizeBitmap
    }

//    private fun getRealPathFromURI(contentUri: Uri): String? {
//        var column_index = 0
//        val proj = arrayOf(MediaStore.Images.Media.DATA)
//        val cursor: Cursor? = contentResolver.query(contentUri, proj, null, null, null)
//        if (cursor.moveToFirst()) {
//            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//        }
//        return cursor.getString(column_index)
//    }

//    private fun exifOrientationToDegrees(exifOrientation: Int): Int {
//        return when (exifOrientation) {
//            ExifInterface.ORIENTATION_ROTATE_90 -> {
//                90
//            }
//            ExifInterface.ORIENTATION_ROTATE_180 -> {
//                180
//            }
//            ExifInterface.ORIENTATION_ROTATE_270 -> {
//                270
//            }
//            else -> 0
//        }
//    }
//
//    private fun rotateBitmap(src: Bitmap, degree: Float): Bitmap? {
//        // Matrix 객체 생성
//        val matrix = Matrix()
//        // 회전 각도 셋팅
//        matrix.postRotate(degree)
//        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
//        return Bitmap.createBitmap(src, 0, 0, src.width,
//                src.height, matrix, true)
//    }

    private fun saveBitmap(bitmap: Bitmap): String {
        var folderPath = Environment.getExternalStorageDirectory().absolutePath + "/path/"
        var fileName = System.currentTimeMillis().toString() + ".jpeg"
        var imagePath = folderPath + fileName

        var folder = File(folderPath)
        if (!folder.isDirectory) folder.mkdirs()

        var out = FileOutputStream(folderPath + fileName)

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)

        return imagePath
    }

}

package com.example.anabada

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.anabada.databinding.ActivitySignupBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern

class SignUpActivity: AppCompatActivity() {

    var signup:SignUpRes? = null
    private val ID_POLICY:String = "4~12자리의 대소문자/숫자만 가능합니다."
    private val NICKNAME_POLICY:String = "2~10자리의 한글/대소문자/숫자만 가능합니다."
    private val PASSWORD_POLICY:String = "8~30자리의 숫자/대문자/소문자/특수문자(!,_) 중 2가지 이상의 조합이어야 합니다."

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
            (binding.id.parent.parent as TextInputLayout).error = null
            (binding.nickname.parent.parent as TextInputLayout).error = null
            (binding.pw.parent.parent as TextInputLayout).error = null
            val uid = binding.id.text.toString()
            val upw = binding.pw.text.toString()
            val nickname = binding.nickname.text.toString()

            if(isValidId(binding.id, uid) and isValidPassword(binding.pw, upw) and isValidNick(binding.nickname, nickname)) {
                service.reqSignUp(uid, upw, nickname).enqueue(object : Callback<SignUpRes> {
                    override fun onFailure(call: Call<SignUpRes>, t: Throwable) {
                        Toast.makeText(this@SignUpActivity, "Failed connection", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SignUpActivity, BoardActivity::class.java)
                        startActivity(intent)
                    }

                    override fun onResponse(call: Call<SignUpRes>, response: Response<SignUpRes>) {
                        signup = response.body()
                        val dialog = AlertDialog.Builder(this@SignUpActivity)
                        dialog.setTitle("success: " + signup?.success.toString())
                        dialog.setMessage("result code: " + signup?.resultCode)
                        dialog.show()
                        Toast.makeText(this@SignUpActivity, "success: " + signup?.success.toString() +
                                "\nresult code: " + signup?.resultCode, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SignUpActivity, BoardActivity::class.java)
                        startActivity(intent)
                    }
                })
            }
        }


    }

    private fun isValidPassword(data: Any, str: String): Boolean {

        var valid = true

        val exp = "^[a-zA-Z]{8,30}\$"
        val num = "/[0-9]/g"
        val lowerCase = "^[a-z]*$"
        val upperCase = "^[A-Z]*$"
        val specialChar = "^[!-]*$"
        val pattern = Pattern.compile(exp)
        val matcher = pattern.matcher(str)
        if (!matcher.matches()) {
            valid = false
        }

        // Set error if required
        if (!valid) {
            setError(data, PASSWORD_POLICY)
        }

        return valid
    }

    private fun isValidId(data: Any, str: String): Boolean {

        var valid = true

        val exp = "^[a-zA-z0-9]{4,12}\$"


        val pattern = Pattern.compile(exp)
        val matcher = pattern.matcher(str)
        if (!matcher.matches()) {
            valid = false
        }

        // Set error if required
        if (!valid) {
            setError(data, ID_POLICY)
        }

        return valid
    }

    private fun isValidNick(data: Any, str: String): Boolean {

        var valid = true

        val exp = "^[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣]{2,10}\$"
        val pattern = Pattern.compile(exp)
        val matcher = pattern.matcher(str)
        if (!matcher.matches()) {
            valid = false
        }

        // Set error if required
        if (!valid) {
            setError(data, NICKNAME_POLICY)
        }

        return valid
    }

    private fun setError(data: Any, error: String?) {
        if (data is TextInputEditText) {
            if (data.parent.parent is TextInputLayout) {
                (data.parent.parent as TextInputLayout).error = error
            } else {
                data.error = error
            }
        }
    }

}
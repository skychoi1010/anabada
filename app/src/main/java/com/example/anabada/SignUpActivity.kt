package com.example.anabada

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
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
    private val PASSWORD_POLICY:String = "비밀번호는 8~30자리의 숫자/영어 대문자/영어 소문자/특수문자(!,_) 중 2가지 이상의 조합이어야 합니다."

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
            val uid = binding.id.text.toString()
            val upw = binding.pw.text.toString()
            val nickname = binding.nickname.text.toString()

            if(isValidPassword(binding.pw)) {
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

    private fun isValidPassword(data: Any, updateUI: Boolean = true): Boolean {

        var valid = true
        val str = data.toString()

        val exp = "^(?=.*[a-z0-9])(?=.*[a-z!_])(?=.*[A-Z!_])(?=.*[A-Z0-9])(?=.*[a-zA-Z])(?=.*[0-9!_]).{8,30}$"
        val pattern = Pattern.compile(exp, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(str)
        if (!matcher.matches()) {
            valid = false
        }

        // Set error if required
        if (updateUI) {
            val error: String? = if (valid) null else PASSWORD_POLICY
            setError(data, error)
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
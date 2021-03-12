package com.example.anabada

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.anabada.databinding.ActivitySignupBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern


class SignUpActivity: AppCompatActivity() {

    var signup:SignUpRes? = null
    private val ID_POLICY:String = "4~12자리의 대소문자/숫자만 가능합니다."
    private val NICKNAME_POLICY:String = "2~10자리의 한글/대소문자/숫자만 가능합니다."
    private val PASSWORD_POLICY:String = "8~30자리의 숫자/대문자/소문자/특수문자(!,_) 중 2가지 이상의 조합이어야 합니다."
    private val MATCH_PASSWORD:String = "비밀번호와 동일하지 않습니다."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val api = ApiService.create()

        binding.confirmPw.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                setError(binding.confirmPw, null)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!binding.pw.text.isNullOrEmpty() && !binding.confirmPw.text.isNullOrEmpty()){
                    if (binding.confirmPw.text.toString() != binding.pw.text.toString()){
                        setError(binding.confirmPw, MATCH_PASSWORD)
                    }
                }
            }
        })

        binding.pw.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                setError(binding.confirmPw, null)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!binding.pw.text.isNullOrEmpty() && !binding.confirmPw.text.isNullOrEmpty()){
                    if (binding.confirmPw.text.toString() != binding.pw.text.toString()){
                        setError(binding.confirmPw, MATCH_PASSWORD)
                    }
                }
            }
        })

        binding.signupBtn.setOnClickListener {
            setError(binding.id, null)
            setError(binding.pw, null)
            setError(binding.nickname, null)

            val uid = binding.id.text.toString()
            val upw = binding.pw.text.toString()
            val nickname = binding.nickname.text.toString()
            val cpw = binding.confirmPw.text.toString()

            if(isValidId(binding.id, uid) and isValidPassword(binding.pw, upw) and isValidNick(binding.nickname, nickname)) {
                api.reqSignUp(uid, upw, nickname).enqueue(object : Callback<SignUpRes> {
                    override fun onFailure(call: Call<SignUpRes>, t: Throwable) {
                        Toast.makeText(this@SignUpActivity, "Failed connection", Toast.LENGTH_SHORT).show()
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

        val lenReg = "^[a-zA-Z0-9!_]{8,30}\$"
        val lowerReg = "[a-z]"
        val upperReg = "[A-Z]"
        val numReg = "[0-9]"
        val specReg = "[!_]"

        var cnt = 0
        if (Pattern.matches(lenReg, str)) {
            if (Pattern.compile(lowerReg).matcher(str).find()) {
                cnt++
            }
            if (Pattern.compile(upperReg).matcher(str).find()) {
                cnt++
            }
            if (Pattern.compile(numReg).matcher(str).find()) {
                cnt++
            }
            if (Pattern.compile(specReg).matcher(str).find()) {
                cnt++
            }

            if (cnt < 2) {
                valid = false
            }
        } else valid = false

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
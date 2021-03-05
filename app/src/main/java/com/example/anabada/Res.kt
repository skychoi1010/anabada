package com.example.anabada

data class LoginRes (
        var success: Boolean,
        var resultCode: String,
        var id: Int,
        var nickname: String
)

data class LogoutRes (
        var success: Boolean,
        var resultCode: String
)

data class SignUpRes (
        var success: Boolean,
        var resultCode: String
)

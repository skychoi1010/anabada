package com.example.anabada.network

import android.content.Context
import com.example.anabada.repo.MySharedPreferences
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ReceivedCookiesInterceptor     // AddCookiesInterceptor()
    (private val context: Context) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        if (originalResponse.headers("Set-Cookie").isNotEmpty()) {
            val cookies = MySharedPreferences.getCookie(context)
            for (header in originalResponse.headers("Set-Cookie")) {
                cookies.add(header)
            }
            MySharedPreferences.setCookie(context, cookies)

        }
        return originalResponse
    }
}
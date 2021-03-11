package com.example.anabada

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("user/login")
    fun reqLogin(
        @Field("uid") uid:String,
        @Field("upw") upw:String
    ): Call<LoginRes>

    @GET("user/logout")
    fun reqLogout(
            @Query("success") success:Boolean,
            @Query("resultCode") resultCode:String
    ): Call<LogoutRes>

    @FormUrlEncoded
    @POST("user/signup")
    fun reqSignUp(
            @Field("uid") uid:String,
            @Field("upw") upw:String,
            @Field("nickname") nickname:String
    ): Call<SignUpRes>

    @GET("board?page=")
    fun reqBoard(
            @Query("success") success:Boolean,
            @Query("resultCode") resultCode:String
    ): Call<BoardPageRes>

    companion object {
        private const val BASE_URL = "https://anabada.du.r.appspot.com/api/"

        fun create(): ApiService {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val headerInterceptor = Interceptor {
                val request = it.request()
                        .newBuilder()
                        .build()
                return@Interceptor it.proceed(request)
            }

            val client = OkHttpClient.Builder()
                    .addInterceptor(headerInterceptor)
                    .addInterceptor(httpLoggingInterceptor)
                    .build()

            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService::class.java)
        }
    }
}
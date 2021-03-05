package com.example.anabada

import retrofit2.Call
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
    fun reqSignup(
            @Field("uid") uid:String,
            @Field("upw") upw:String,
            @Field("nickname") nickname:String
    ): Call<SignupRes>

}
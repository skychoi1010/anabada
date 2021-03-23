package com.example.anabada

import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.net.CookieManager

interface ApiService {

    @FormUrlEncoded
    @POST("user/login")
    fun reqLogin(
        @Field("uid") uid:String,
        @Field("upw") upw:String
    ): Call<LoginRes>

    @GET("user/logout")
    fun reqLogout(): Call<LogoutRes>

    @FormUrlEncoded
    @POST("user/signup")
    fun reqSignUp(
            @Field("uid") uid:String,
            @Field("upw") upw:String,
            @Field("nickname") nickname:String
    ): Call<SignUpRes>

    @GET("board")
    fun reqBoard(
            @Query("page") page:Int
    ): Call<BoardPageRes>

    @GET("board/{id}")
    fun reqBoardDetail(
        @Path("id") id:Int
    ): Call<BoardDetailRes>

    @GET("board/{id}/comment")
    fun reqComment(
        @Path("id") id:Int,
        @Query("page") page: Int
    ): Call<CommentData>

    @Multipart
    @POST("image")
    fun reqPostImage(
        @Part image:MultipartBody.Part
    ): Call<PostImageRes>

    @FormUrlEncoded
    @POST("board")
    fun reqPostContent(
        @Field("title") title: String,
        @Field("price") price: Int,
        @Field("contents") contents: String,
        @Field("imgId") imgId: Int
    ): Call<PostContentRes>

    @FormUrlEncoded
    @PUT("board/{id}")
    fun reqReviseContent(
        @Path("id") id: Int,
        @Field("title") title: String,
        @Field("price") price: Int,
        @Field("contents") contents: String,
        @Field("imgId") imgId: Int //TODO not confirmed
    ): Call<ReviseContentRes>

    @FormUrlEncoded
    @DELETE("board/{id}")
    fun reqDeleteContent(
        @Field("id") id: Int
    ): Call<DeleteContentRes>

    @FormUrlEncoded
    @POST("coment")
    fun reqPostComment(
        @Field("boardId") boardId: Int,
        @Field("contents") contents: String
    ): Call<PostCommentRes>

    @FormUrlEncoded
    @PUT("coment/{id}")
    fun reqReviseComment(
        @Path("contents") contents: String
    ): Call<ReviseCommentRes>

    @FormUrlEncoded
    @DELETE("coment/{id}")
    fun reqDeleteComment(
        @Path("id") id: Int
    ): Call<DeleteCommentRes>

    companion object {
        private const val BASE_URL = "https://anabada.du.r.appspot.com/api/"

        fun create(): ApiService {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                    .cookieJar(JavaNetCookieJar(CookieManager()))
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
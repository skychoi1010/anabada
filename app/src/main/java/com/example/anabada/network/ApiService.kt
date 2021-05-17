package com.example.anabada.network

import android.content.Context
import okhttp3.JavaNetCookieJar
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.net.CookieManager
import java.net.CookiePolicy

interface ApiService {

    @FormUrlEncoded
    @POST("user/login")
    suspend fun reqLogin(
        @Field("uid") uid: String,
        @Field("upw") upw: String
    ): Response<LoginRes>

    @GET("user/logout")
    suspend fun reqLogout(): Response<LogoutRes>

    @FormUrlEncoded
    @POST("user/signup")
    suspend fun reqSignUp(
        @Field("uid") uid: String,
        @Field("upw") upw: String,
        @Field("nickname") nickname: String
    ): Response<SignUpRes>

    @GET("board")
    suspend fun reqBoard(
        @Query("page") page: Int
    ): Response<BoardPageRes>

    @GET("board/{id}")
    suspend fun reqBoardDetail(
        @Path("id") id: Int
    ): Response<BoardDetailRes>

    @GET("board/{id}/comment")
    suspend fun reqComment(
        @Path("id") id: Int,
        @Query("page") page: Int
    ): Response<CommentRes>

    @Multipart
    @POST("image")
    suspend fun reqPostImage(
        @Part image: MultipartBody.Part
    ): Response<PostImageRes>

    @FormUrlEncoded
    @POST("board")
    suspend fun reqPostContent(
        @Field("title") title: String,
        @Field("price") price: Int,
        @Field("contents") contents: String,
        @Field("imgId") imgId: Int
    ): Response<PostContentRes>

    @FormUrlEncoded
    @PUT("board/{id}")
    suspend fun reqEditContent(
        @Path("id") id: Int,
        @Field("title") title: String,
        @Field("price") price: Int,
        @Field("contents") contents: String,
        @Field("imgId") imgId: Int //TODO not confirmed
    ): Response<EditContentRes>

    @DELETE("board/{id}")
    suspend fun reqDeleteContent(
        @Path("id") id: Int
    ): Response<DeleteContentRes>

    @FormUrlEncoded
    @POST("comment")
    suspend fun reqPostComment(
        @Field("boardId") boardId: Int,
        @Field("contents") contents: String
    ): Response<PostCommentRes>

    @FormUrlEncoded
    @PUT("comment/{id}")
    suspend fun reqEditComment(
        @Path("id") id: Int,
        @Field("contents") contents: String
    ): Response<EditCommentRes>

    @DELETE("comment/{id}")
    suspend fun reqDeleteComment(
        @Path("id") id: Int
    ): Response<DeleteCommentRes>

    companion object {
        private const val BASE_URL = "https://anabada.du.r.appspot.com/api/"
        private const val BASE_URL_IMG = "http://175.113.223.199:8080/api/"

        fun create(context: Context): ApiService {
            val httpLoggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val cookieManager = CookieManager()
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
            val client = OkHttpClient.Builder()
                .cookieJar(JavaNetCookieJar(cookieManager))
                .addInterceptor(AddCookiesInterceptor(context))
                .addInterceptor(ReceivedCookiesInterceptor(context))
                .addInterceptor(httpLoggingInterceptor)
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }

        fun createImg(): ApiService {
            val httpLoggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val cookieManager = CookieManager()
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
            val client = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL_IMG)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
package com.example.anabada.network

import android.os.Parcelable
import com.example.anabada.db.model.BoardsData
import kotlinx.android.parcel.Parcelize

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

data class BoardPageRes (
        var success: Boolean,
        var resultCode: String,
        var boards: ArrayList<BoardsData>
        )

//@Parcelize
//data class BoardsData (
//        var id: Int,
//        var title: String,
//        var price: Int,
//        var author: String,
//        var thumbImg: String,
//        var detailImg: String,
//        val isMine: Boolean,
//        var date: String,
//        var commentCount: Int
//        ) : Parcelable

data class BoardDetailRes (
        var resultCode: String,
        var board: BoardDetail
        )

data class BoardDetail (
        var id: Int,
        var title: String,
        var contents: String,
        var price: Int,
        var author: String,
        var detailImg: String,
        var isMine: Boolean,
        var date: String,
        var commentCount: Int
        )

data class CommentRes (
        var success: Boolean,
        var resultCode: String,
        var comments: ArrayList<CommentDetail>
        )

data class CommentDetail (
        var id: Int,
        var author: String,
        var contents: String,
        var isMine: Boolean,
        var date: String
        )

data class PostImageRes (
        var success: Boolean,
        var resultCode: String,
        var id: Int
        )

data class PostContentRes (
        var resultCode: String,
        var id: Int
        )

data class EditContentRes (
        var resultCode: String,
        var id: Int
        )

data class DeleteContentRes (
        var resultCode: String,
        var id: Int
        )

data class PostCommentRes (
        var success: Boolean,
        var resultCode: String,
        var id: Int
        )

data class EditCommentRes (
        var resultCode: String,
        var id: Int
        )

data class DeleteCommentRes (
        var resultCode: String,
        var id: Int
        )


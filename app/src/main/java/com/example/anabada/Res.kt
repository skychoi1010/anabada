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

data class BoardPageRes (
        var success: Boolean,
        var resultCode: String,
        var boards: ArrayList<BoardsData>
        )

data class BoardsData (
        var id: Int,
        var title: String,
        var date: String,
        var price: Int,
        var author: String,
        var thumbImg: String
        )

data class BoardDetailRes (
        var result: String,
        var board: ArrayList<BoardDetail>,
        var comments: ArrayList<CommentData>
        )

data class BoardDetail (
        var id: Int,
        var title: String,
        var date: String,
        var price: Int,
        var author: String,
        var isMyBoard: Boolean,
        var contents: String,
        var detailImg: String
        )

data class CommentData (
        var id: Int,
        var author: String,
        var contents: String,
        var date: String,
        var isMyComment: Boolean
        )

data class PostImageRes (
        var success: Boolean,
        var result: String,
        var id: Int
        )

data class PostContentRes (
        var result: String,
        var id: Int
        )

data class ReviseContentRes (
        var result: String,
        var id: Int
        )

data class DeleteContentRes (
        var result: String,
        var id: Int
        )

data class PostCommentRes (
        var result: String,
        var id: Int
        )

data class ReviseCommentRes (
        var result: String,
        var id: Int
        )

data class DeleteCommentRes (
        var result: String,
        var id: Int
        )

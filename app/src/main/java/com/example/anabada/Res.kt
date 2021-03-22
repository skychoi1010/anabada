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
        var author: String,
        var title: String,
        var contents: String,
        var price: Int,
        var thumbImg: String,
        var isDel: Boolean,
        var createdAt: String,
        var updatedAt: String,
        var imageId: String,
        var userId: String
        )

data class BoardDetailRes (
        var resultCode: String,
        var board: DataValues,
        var comments: ArrayList<CommentData>
        )

data class DataValues (var dataValues: BoardDetail)

data class BoardDetail (
        var id: Int,
        var title: String,
        var createdAt: String,
        var updatedAt: String,
        var price: Int,
        var author: String,
        var isMyBoard: Boolean,
        var contents: String,
        var detailImg: String,
        var isDel: Boolean,
        var userId: String
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
        var resultCode: String,
        var id: Int
        )

data class PostContentRes (
        var resultCode: String,
        var id: Int
        )

data class ReviseContentRes (
        var resultCode: String,
        var id: Int
        )

data class DeleteContentRes (
        var resultCode: String,
        var id: Int
        )

data class PostCommentRes (
        var resultCode: String,
        var id: Int
        )

data class ReviseCommentRes (
        var resultCode: String,
        var id: Int
        )

data class DeleteCommentRes (
        var resultCode: String,
        var id: Int
        )
